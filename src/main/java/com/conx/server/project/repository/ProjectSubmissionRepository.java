package com.conx.server.project.repository;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectSubmissionRepository
        extends JpaRepository<ProjectSubmission, Long> {

    /**
     * 프로젝트의 특정 상태 중 가장 최근 제출물 조회
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndStatusOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus status
    );

    /**
     * DRAFT를 제외한 프로젝트 제출 이력 조회
     */
    Page<ProjectSubmission>
    findAllByProjectIdAndStatusNotOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus excludedStatus,
            Pageable pageable
    );

    /**
     * 프로젝트에 속한 특정 제출물 조회
     */
    Optional<ProjectSubmission>
    findByIdAndProjectId(
            Long submissionId,
            Long projectId
    );

}