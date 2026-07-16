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
     * 프로젝트의 가장 최근 제출물 조회
     */
    Optional<ProjectSubmission>
    findTopByProjectIdOrderByIdDesc(
            Long projectId
    );

    /**
     * 프로젝트의 특정 상태 중 가장 최근 제출물 조회
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndStatusOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus status
    );

    /**
     * 특정 크루가 작성한 가장 최근 DRAFT 조회
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndAuthorCrewIdAndStatusOrderByIdDesc(
            Long projectId,
            Long authorCrewId,
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
     * 특정 상태의 프로젝트 제출 이력 조회
     */
    Page<ProjectSubmission>
    findAllByProjectIdAndStatusOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus status,
            Pageable pageable
    );

    /**
     * 모든 상태의 프로젝트 제출 이력 조회
     *
     * 기존 dev 서비스 호출부 호환용이다.
     */
    Page<ProjectSubmission>
    findAllByProjectOrderByIdDesc(
            Project project,
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
     * DRAFT를 제외한 가장 최근 제출물 조회
     */
    Optional<ProjectSubmission>
    findTopByProjectIdAndStatusNotOrderByIdDesc(
            Long projectId,
            ProjectSubmissionStatus excludedStatus
    );

    /**
     * 기존 서비스 코드 호환용
     *
     * 다건 제출 구조이므로 단순 findByProject 파생 쿼리를
     * 사용하지 않고 가장 최근 제출물을 반환한다.
     */
    default Optional<ProjectSubmission> findByProject(
            Project project
    ) {
        return findTopByProjectIdOrderByIdDesc(
                project.getId()
        );
    }

    /**
     * 특정 상태의 가장 최근 제출물 조회
     */
    default Optional<ProjectSubmission> findByProjectAndStatus(
            Project project,
            ProjectSubmissionStatus status
    ) {
        return findTopByProjectIdAndStatusOrderByIdDesc(
                project.getId(),
                status
        );
    }

    /**
     * 기존 dev 페이징 호출부 호환용
     */
    default Page<ProjectSubmission> findByProject(
            Project project,
            Pageable pageable
    ) {
        return findAllByProjectOrderByIdDesc(
                project,
                pageable
        );
    }

    /**
     * 기업이 특정 결과물에 피드백을 등록할 때 사용하는 잠금 조회
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