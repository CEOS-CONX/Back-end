# BackEnd
## Github Actions
- gradle build
- docker hub에 이미지 업로드
- EC2 배포

## Branch
- main
    - 최종 배포용도의 브랜치
    - main에 push될 경우 CI/CD 파이프라인이 동작합니다.
- dev
    - 최종 배포 이전의 브랜치. 배포 직전 테스트 및 코드 합치기 과정을 수행
    - dev에 push될 경우 CI 파이프라인만 동작합니다.
- feature
    - 개발 목적의 브랜치

## Github Secrets
- EC2_HOST
    - EC2서버의 IP주소입니다
    - 팀 AWS 계정 생성 전까지 임시계정을 사용 중입니다
- EC2_KEY
    - EC2 서버에 접속하기 위한 pem 키입니다.
    - 팀 AWS 계정 생성 전까지 임시 키를 사용 중입니다
- DB_URL / DB_PASSWORD
    - 데이터베이스 URL 및 비밀번호입니다.
- DOCKERHUB_USERNAME / DOCKERHUB_PASSWORD
    - 도커허브에 접속하기 위한 아이디 및 비밀번호입니다. 도커 아이디 비밀번호와 동일합니다.