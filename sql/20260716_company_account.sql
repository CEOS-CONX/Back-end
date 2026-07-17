/*
 * CONX 기업 마이페이지·계정 관리 DB Migration
 *
 * 작성일: 2026-07-16
 *
 * 주의:
 * - 운영 또는 개발 MySQL DB에 실행한다.
 * - 로컬 테스트 환경은 H2 create-drop이므로 직접 실행하지 않는다.
 * - 실행 전 company 테이블에 동일한 컬럼이 존재하는지 확인한다.
 */


/* =========================================================
 * 1. 기업 대표 연락처 추가
 * ========================================================= */

ALTER TABLE company
    ADD COLUMN representative_phone VARCHAR(255) NULL,
    ADD COLUMN representative_email VARCHAR(255) NULL;