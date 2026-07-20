#!/usr/bin/env bash
# - app_blue → 127.0.0.1:8081,
# - app_green → 127.0.0.1:8082
# - root로 실행되어야함
#
# 구동명령어:
# sudo ./deploy.sh init               최초 첫 번째 구동 시 사용. nginx 설정 읽어오면서 blue에 배포
# sudo ./deploy.sh deploy [IMG_TAG]   새 버전 배포 시 사용. idle 컬러에 새 버전을 띄우고 헬스체크 후 전환. 기존 active 컬러는 30초 후 종료
# sudo ./deploy.sh rollback           직전 active 컬러로 되돌림
# sudo ./deploy.sh status             현재 각 컬러들의 상태 출력

set -euo pipefail

# 설정값
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.yml"

NGINX_CONF_DIR="/etc/nginx/conf.d"
NGINX_UPSTREAM_DIR="/etc/nginx/upstreams"

ACTIVE_LINK="${NGINX_CONF_DIR}/upstream_active.conf"
SAMPLE_CONF_DIR="${SCRIPT_DIR}/nginx-conf.d-sample"

declare -A APP_PORT=( [blue]=8081 [green]=8082 )
HEALTH_PATH="/health/ready"
HEALTH_RETRIES=60
HEALTH_INTERVAL=2
IMAGE_NAME="${APP_IMAGE_NAME:-myapp}"

# 로그
GREEN='\033[0;32m'; RED='\033[0;31m'; YELLOW='\033[1;33m'; NC='\033[0m'
log()  { echo -e "${GREEN}[deploy]${NC} $*"; }
warn() { echo -e "${YELLOW}[deploy]${NC} $*"; }
err()  { echo -e "${RED}[deploy]${NC} $*" >&2; }

# root 여부 조사
require_root() {
    if [[ "${EUID}" -ne 0 ]]; then
        err "sudo로 실행되지 않았습니다."
        exit 1
    fi
}

# 도커 컴포즈 작동
dc() { docker compose -f "$COMPOSE_FILE" "$@"; }

# 최초 1회 셋업
cmd_init() {
    require_root

    mkdir -p "$NGINX_UPSTREAM_DIR"

    cp "${SAMPLE_CONF_DIR}/upstream_blue.conf" \
        "${NGINX_UPSTREAM_DIR}/upstream_blue.conf"

    cp "${SAMPLE_CONF_DIR}/upstream_green.conf" \
        "${NGINX_UPSTREAM_DIR}/upstream_green.conf"

    if [[ ! -e "$ACTIVE_LINK" ]]; then
        ln -sfn "${NGINX_UPSTREAM_DIR}/upstream_green.conf" "$ACTIVE_LINK"
        log "초기 active를 green으로 설정"
    else
        warn "upstream_active.conf 가 이미 존재합니다."
    fi

    nginx -t
    systemctl reload nginx

    log "초기 설정 완료"
}

# 현재 active 컬러 조회
get_active_color() {
    if [[ ! -L "$ACTIVE_LINK" ]]; then
        err "심볼릭 링크(${ACTIVE_LINK})가 없음. 최초 실행부터 진행해야합니다."
        exit 1
    fi
    local target
    target=$(readlink -f "$ACTIVE_LINK")
    if [[ "$target" == *upstream_blue.conf ]]; then
        echo "blue"
    elif [[ "$target" == *upstream_green.conf ]]; then
        echo "green"
    else
        err "알 수 없는 active 대상: $target"
        exit 1
    fi
}

# 반대 컬러 가져오기
opposite_color() {
    [[ "$1" == "blue" ]] && echo "green" || echo "blue"
}

# 유효 컨테이너 헬스체크
health_check() {
    local color="$1"
    local port="${APP_PORT[$color]}"
    local attempt=1

    log "헬스체크 시작: 127.0.0.1:${port}${HEALTH_PATH}"
    while (( attempt <= HEALTH_RETRIES )); do
        if curl -fsS --max-time 3 "http://127.0.0.1:${port}${HEALTH_PATH}" >/dev/null 2>&1; then
            log "헬스체크 성공 (${attempt}/${HEALTH_RETRIES})"
            return 0
        fi
        warn "헬스체크 대기 중... (${attempt}/${HEALTH_RETRIES})"
        sleep "$HEALTH_INTERVAL"
        ((attempt++))
    done

    err "헬스체크 실패: ${color} (127.0.0.1:${port}) 가 정상 응답하지 않습니다."
    return 1
}

# 스위치
switch_traffic() {
    local new_color="$1"
    log "트래픽 전환: ${ACTIVE_LINK} -> upstream_${new_color}.conf"
    ln -sfn "${NGINX_UPSTREAM_DIR}/upstream_${new_color}.conf" "$ACTIVE_LINK"

    if ! nginx -t; then
        err "nginx 설정 검증 실패. 전환을 취소합니다."
        return 1
    fi
    systemctl reload nginx
    log "nginx reload 완료. 현재 active: ${new_color}"
}

# 메인배포로직
cmd_deploy() {
    require_root
    mkdir -p logs/blue logs/green
    chown -R ubuntu:ubuntu logs
    local tag="${1:-latest}"
    export APP_IMAGE="${IMAGE_NAME}:${tag}"

    local active idle
    active=$(get_active_color)
    idle=$(opposite_color "$active")

    log "active: ${active} / idle: ${idle}"
    log "이미지: ${APP_IMAGE}"

    dc pull "app_${idle}" || warn "pull 실패"

    log "idle 컨테이너(${idle}) 재기동"
    dc up -d --no-deps --force-recreate "app_${idle}"

    if ! health_check "$idle"; then
        dc stop "app_${idle}"
        err "배포 실패: idle 컨테이너가 정상화되지 않았습니다. 기존 ${active} 는 계속 서비스 중입니다."
        err "문제 확인 후 'docker compose -f ${COMPOSE_FILE} logs app_${idle}' 로 로그를 확인하세요."
        exit 1
    fi

    if ! switch_traffic "$idle"; then
        dc stop "app_${idle}"
        err "트래픽 전환 실패. 기존 ${active} 로 유지됩니다."
        exit 1
    fi

    log "안정성 확인. 이전 컨테이너 종료"
    dc stop "app_${active}"

    log "배포 완료: ${active} -> ${idle}"
}

# 롤백
cmd_rollback() {
    require_root
    local active idle
    active=$(get_active_color)
    idle=$(opposite_color "$active")

    warn "롤백 실행: ${active} -> ${idle}"

    dc up -d --no-deps "app_${idle}"
    if ! health_check "$idle"; then
        dc stop "app_${idle}"
        err "롤백 대상(${idle}) 비정상, 수동 확인이 필요"
        exit 1
    fi

    switch_traffic "$idle"
    log "롤백 완료. 현재 active: ${idle}"
}

# 상태조회
cmd_status() {
    local active idle
    active=$(get_active_color)
    idle=$(opposite_color "$active")

    echo "Active: $active (port ${APP_PORT[$active]})"
    echo "Idle: $idle (port ${APP_PORT[$idle]})"
    echo
    dc ps
}

# 메인
main() {
    case "${1:-}" in
        init)
            cmd_init
            ;;
        deploy)
            shift
            cmd_deploy "${1:-latest}"
            ;;
        rollback)
            cmd_rollback
            ;;
        status)
            cmd_status
            ;;
        *)
            echo "help: $0 {init|deploy [tag]|rollback|status}"
            exit 1
            ;;
    esac
}

main "$@"