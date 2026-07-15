package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectApplicationRepository
        extends JpaRepository<ProjectApplication, Long> {

    List<ProjectApplication> findAllByProjectId(
            Long projectId
    );

    Optional<ProjectApplication> findByIdAndProjectId(
            Long applicationId,
            Long projectId
    );

    List<ProjectApplication> findAllByProjectIdAndStatus(
            Long projectId,
            ProjectApplicationStatus status
    );

    int countByCrew(
            Crew crew
    );

    long countByCrewAndStatus(
            Crew crew,
            ProjectApplicationStatus status
    );

    boolean existsByProjectIdAndCrewId(
            Long projectId,
            Long crewId
    );

    Optional<ProjectApplication> findByProjectIdAndCrewId(
            Long projectId,
            Long crewId
    );

    @Query("""
            select new com.conx.server.project.dto.response.ProjectApplicationWrapperDTO(
                project.id,
                application.id,
                project.projectType,
                application.createdAt,
                application.status
            )
            from ProjectApplication application
            join application.project project
            where application.crew = :crew
            order by application.createdAt
            """)
    List<ProjectApplicationWrapperDTO>
    findProjectApplicationByCrew(
            @Param("crew")
            Crew crew
    );

    @Query("""
            select new com.conx.server.project.dto.response.ProjectApplicationWrapperDTO(
                project.id,
                application.id,
                project.projectType,
                application.createdAt,
                application.status
            )
            from ProjectApplication application
            join application.project project
            where application.crew = :crew
              and application.status = :status
            order by application.createdAt
            """)
    List<ProjectApplicationWrapperDTO>
    findProjectApplicationByCrewAndStatus(
            @Param("crew")
            Crew crew,

            @Param("status")
            ProjectApplicationStatus status
    );

    @Query(
            value = """
                    select application
                    from ProjectApplication application
                    join fetch application.project project
                    join fetch project.company company
                    where application.crew.id = :crewId
                      and application.status <>
                          com.conx.server.project.domain.enums.ProjectApplicationStatus.REJECTED

                      and (
                            :keyword is null
                            or project.name like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                            or company.companyName like concat('%', :keyword, '%')
                      )

                      and (
                            :category is null
                            or company.industry = :category
                      )

                      and (
                            :projectType is null
                            or project.projectType = :projectType
                      )

                      and (
                            :startDate is null
                            or project.projectStartDate >= :startDate
                      )

                      and (
                            :endDate is null
                            or project.projectDeadline <= :endDate
                      )

                      and (
                            :workspaceStatus is null

                            or (
                                :workspaceStatus = 'APPLIED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.PENDING
                            )

                            or (
                                :workspaceStatus = 'IN_PROGRESS'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status in (
                                    com.conx.server.project.domain.enums.ProjectStatus.CONTRACT_PENDING,
                                    com.conx.server.project.domain.enums.ProjectStatus.PROGRESS
                                )
                            )

                            or (
                                :workspaceStatus = 'EXECUTION_COMPLETED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status =
                                    com.conx.server.project.domain.enums.ProjectStatus.WAITING_RESULT
                            )

                            or (
                                :workspaceStatus = 'SUBMISSION_COMPLETED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status in (
                                    com.conx.server.project.domain.enums.ProjectStatus.INSPECTION,
                                    com.conx.server.project.domain.enums.ProjectStatus.ADJUSTING
                                )
                            )

                            or (
                                :workspaceStatus = 'SETTLEMENT_COMPLETED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status =
                                    com.conx.server.project.domain.enums.ProjectStatus.DONE
                                and exists (
                                    select settlement.id
                                    from ProjectSettlement settlement
                                    where settlement.project = project
                                      and settlement.crew = application.crew
                                      and settlement.status =
                                          com.conx.server.project.domain.enums.ProjectSettlementStatus.PAID
                                )
                            )
                      )
                    """,
            countQuery = """
                    select count(application)
                    from ProjectApplication application
                    join application.project project
                    join project.company company
                    where application.crew.id = :crewId
                      and application.status <>
                          com.conx.server.project.domain.enums.ProjectApplicationStatus.REJECTED

                      and (
                            :keyword is null
                            or project.name like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                            or company.companyName like concat('%', :keyword, '%')
                      )

                      and (
                            :category is null
                            or company.industry = :category
                      )

                      and (
                            :projectType is null
                            or project.projectType = :projectType
                      )

                      and (
                            :startDate is null
                            or project.projectStartDate >= :startDate
                      )

                      and (
                            :endDate is null
                            or project.projectDeadline <= :endDate
                      )

                      and (
                            :workspaceStatus is null

                            or (
                                :workspaceStatus = 'APPLIED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.PENDING
                            )

                            or (
                                :workspaceStatus = 'IN_PROGRESS'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status in (
                                    com.conx.server.project.domain.enums.ProjectStatus.CONTRACT_PENDING,
                                    com.conx.server.project.domain.enums.ProjectStatus.PROGRESS
                                )
                            )

                            or (
                                :workspaceStatus = 'EXECUTION_COMPLETED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status =
                                    com.conx.server.project.domain.enums.ProjectStatus.WAITING_RESULT
                            )

                            or (
                                :workspaceStatus = 'SUBMISSION_COMPLETED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status in (
                                    com.conx.server.project.domain.enums.ProjectStatus.INSPECTION,
                                    com.conx.server.project.domain.enums.ProjectStatus.ADJUSTING
                                )
                            )

                            or (
                                :workspaceStatus = 'SETTLEMENT_COMPLETED'
                                and application.status =
                                    com.conx.server.project.domain.enums.ProjectApplicationStatus.SELECTED
                                and project.status =
                                    com.conx.server.project.domain.enums.ProjectStatus.DONE
                                and exists (
                                    select settlement.id
                                    from ProjectSettlement settlement
                                    where settlement.project = project
                                      and settlement.crew = application.crew
                                      and settlement.status =
                                          com.conx.server.project.domain.enums.ProjectSettlementStatus.PAID
                                )
                            )
                      )
                    """
    )
    Page<ProjectApplication> findCrewWorkspaceProjects(
            @Param("crewId")
            Long crewId,

            @Param("keyword")
            String keyword,

            @Param("workspaceStatus")
            String workspaceStatus,

            @Param("category")
            Industry category,

            @Param("projectType")
            ProjectType projectType,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate,

            Pageable pageable
    );
}