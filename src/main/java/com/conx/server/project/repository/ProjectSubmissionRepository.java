package com.conx.server.project.repository;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectSubmissionRepository
        extends JpaRepository<ProjectSubmission, Long> {

    /**
     * 프로젝트의 가장 최근 제출물
     */
    Optional<ProjectSubmission>
    findTopByProjectIdOrderByIdDesc(
            Long projectId
    );

    /**
     * 프로젝트의 특정 상태 중 가장 최근 제출물
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndStatusOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus status
    );

    /**
     * 특정 크루가 작성한 프로젝트 임시 저장 제출물
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndAuthorCrewIdAndStatusOrderByIdDesc(
            Long projectId,
            Long authorCrewId,
            ProjectSubmissionStatus status
    );

    /**
     * 프로젝트별 제출 내역
     *
     * 임시 저장 상태는 목록에서 제외할 때 사용한다.
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

    /**
     * 기존 서비스 코드 호환용
     *
     * 한 프로젝트에 여러 결과물이 존재하므로
     * 가장 최근 결과물을 반환한다.
     */
    default Optional<ProjectSubmission>
    findByProject(
            Project project
    ) {
        return findTopByProjectIdOrderByIdDesc(
                project.getId()
        );
    }

    /**
     * 기존 기업 검수 코드 호환용
     */
    default Optional<ProjectSubmission>
    findByProjectAndStatus(
            Project project,
            ProjectSubmissionStatus status
    ) {
        return findTopByProjectIdAndStatusOrderByIdDesc(
                project.getId(),
                status
        );
    }

    /**
     * 임시 저장을 제외한 가장 최근 결과물
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndStatusNotOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus excludedStatus
    );

    /**
     * 기업이 특정 제출물을 검수할 때 사용하는 잠금 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select submission
        from ProjectSubmission submission
        join fetch submission.project project
        where submission.id = :submissionId
          and project.id = :projectId
        """)
    Optional<ProjectSubmission>
    findByIdAndProjectIdForUpdate(
            @Param("submissionId")
            Long submissionId,

            @Param("projectId")
            Long projectId
    );
}