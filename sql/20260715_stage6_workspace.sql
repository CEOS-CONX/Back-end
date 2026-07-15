/*
 * CONX Stage 6 DB Migration
 *
 * 주의:
 * 운영 또는 개발 DB의 실제 테이블 구조를 확인한 후 실행해야 한다.
 * 로컬 테스트 환경은 H2이므로 이 파일을 로컬에서 실행하지 않는다.
 */


/* =========================================================
 * 1. 크루 지급 확인 상태
 * ========================================================= */

ALTER TABLE project_settlement
    ADD COLUMN crew_payment_status VARCHAR(30) NULL,
    ADD COLUMN crew_payment_confirmed_date DATE NULL;

UPDATE project_settlement
SET crew_payment_status = 'BEFORE_PAYMENT'
WHERE crew_payment_status IS NULL;


/* =========================================================
 * 2. 결과물 제출 정보 추가
 * ========================================================= */

ALTER TABLE project_submission
    ADD COLUMN author_crew_id BIGINT NULL,
    ADD COLUMN title VARCHAR(255) NULL,
    ADD COLUMN submitted_at DATETIME(6) NULL;


/*
 * 기존 결과물 작성 크루 보정
 */
UPDATE project_submission submission
    JOIN project project
ON project.id = submission.project_id
    SET submission.author_crew_id = project.selected_crew_id
WHERE submission.author_crew_id IS NULL;


/*
 * 기존 결과물 제목과 제출 시각 보정
 */
UPDATE project_submission
SET title = '기존 결과물 제출'
WHERE title IS NULL
   OR TRIM(title) = '';

UPDATE project_submission
SET submitted_at = created_at
WHERE submitted_at IS NULL
  AND status <> 'DRAFT';


/* =========================================================
 * 3. 작성 크루 외래 키
 * ========================================================= */

CREATE INDEX idx_project_submission_author_crew
    ON project_submission(author_crew_id);

ALTER TABLE project_submission
    ADD CONSTRAINT fk_project_submission_author_crew
        FOREIGN KEY (author_crew_id)
            REFERENCES crew(id);


/* =========================================================
 * 4. 결과물 참고 링크 테이블
 * ========================================================= */

CREATE TABLE project_submission_reference_link (
                                                   project_submission_id BIGINT NOT NULL,
                                                   reference_link VARCHAR(255) NOT NULL,

                                                   CONSTRAINT fk_submission_reference_link
                                                       FOREIGN KEY (project_submission_id)
                                                           REFERENCES project_submission(id)
                                                           ON DELETE CASCADE
);

CREATE INDEX idx_submission_reference_link
    ON project_submission_reference_link(
                                         project_submission_id
        );


/* =========================================================
 * 5. 프로젝트별 결과물 다건 저장
 * ========================================================= */

/*
 * project_submission.project_id에 걸린 UNIQUE 인덱스를 제거해야 한다.
 *
 * 운영 DB에서 아래 명령으로 인덱스 이름 확인:
 *
 * SHOW INDEX FROM project_submission;
 *
 * 확인한 실제 인덱스 이름으로 아래 구문을 실행한다.
 *
 * ALTER TABLE project_submission
 *     DROP INDEX 실제_UNIQUE_인덱스_이름;
 */