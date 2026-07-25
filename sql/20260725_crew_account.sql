/*
 * CONX 크루 계정 정보 DB Migration
 *
 * 작성일: 2026-07-25
 *
 * 주의:
 * - 운영 또는 개발 MySQL DB에 실행한다.
 * - 로컬 테스트 환경은 H2 create-drop이므로 직접 실행하지 않는다.
 * - 실행 전 crew 테이블에 동일한 컬럼이 존재하는지 확인한다.
 */

ALTER TABLE crew
    ADD COLUMN representative_email VARCHAR(255) NULL;
