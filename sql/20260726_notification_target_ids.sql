ALTER TABLE notification
    ADD COLUMN project_id BIGINT NULL,
    ADD COLUMN question_id BIGINT NULL,
    ADD COLUMN application_id BIGINT NULL,
    ADD COLUMN submission_id BIGINT NULL,
    ADD COLUMN settlement_id BIGINT NULL;
