package com.conx.server.project.repository;

import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.response.TodoProjectInfoDTO;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.dto.crew.response.CrewProjectInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndCompanyId(Long projectId, Long companyId);

    Optional<Project> findByIdAndCompanyIdAndStatus(Long projectId, Long companyId, ProjectStatus status);

    long countByCompanyIdAndStatusNot(Long companyId, ProjectStatus status);

    long countByCompanyIdAndStatus(Long companyId, ProjectStatus status);

    @Query("""
        select new com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO(
            p.id, p.projectImage, p.name, p.company.companyName, p.company.industry, p.projectType,
            p.projectStartDate, p.projectDeadline
        )
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and p.company.industry = :category
        order by p.views
    """)
    List<ProjectWrapperForLandingPageDTO> findActiveProjectByCategoryWithViews(
            @Param("category") Industry category
    );

    @Query("""
        select new com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO(
            p.id, p.projectImage, p.name, p.company.companyName, p.company.industry, p.projectType,
            p.projectStartDate, p.projectDeadline
        )
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        order by p.views
    """)
    List<ProjectWrapperForLandingPageDTO> findAllActiveProjectWithViews();

    @Query("""
        select p from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and p.projectDeadline in :deadlines
    """)
    List<Project> findAllAboutDeadline(
            @Param("deadlines") List<LocalDate> deadlines
    );

    @Query("""
    select p
    from Project p
    where p.company.id = :companyId
    and p.status <> com.conx.server.project.domain.enums.ProjectStatus.DRAFT
    and (:keyword is null or p.name like concat('%', :keyword, '%'))
    and (:projectType is null or p.projectType = :projectType)
    and (:startDate is null or p.projectStartDate >= :startDate)
    and (:endDate is null or p.projectDeadline <= :endDate)
    order by p.id desc
""")
    List<Project> findCompanyProjectsByFilter(
            @Param("companyId") Long companyId,
            @Param("keyword") String keyword,
            @Param("projectType") ProjectType projectType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(
            value = """
        select p
        from Project p
        join fetch p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.name like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.projectStartDate >= :startDate)
        and (:endDate is null or p.projectDeadline <= :endDate)
        order by p.id desc
        """,
            countQuery = """
        select count(p)
        from Project p
        join p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.name like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.projectStartDate >= :startDate)
        and (:endDate is null or p.projectDeadline <= :endDate)
        """
    )
    Page<Project> findBrowseProjectsOrderByRecent(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("projectType") ProjectType projectType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query(
            value = """
        select p
        from Project p
        join fetch p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.name like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.projectStartDate >= :startDate)
        and (:endDate is null or p.projectDeadline <= :endDate)
        order by p.views desc, p.id desc
        """,
            countQuery = """
        select count(p)
        from Project p
        join p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.name like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.projectStartDate >= :startDate)
        and (:endDate is null or p.projectDeadline <= :endDate)
        """
    )
    Page<Project> findBrowseProjectsOrderByPopular(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("projectType") ProjectType projectType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query(
            value = """
        select p
        from Project p
        join fetch p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.name like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.projectStartDate >= :startDate)
        and (:endDate is null or p.projectDeadline <= :endDate)
        order by p.views desc, p.id desc
        """,
            countQuery = """
        select count(p)
        from Project p
        join p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.name like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.projectStartDate >= :startDate)
        and (:endDate is null or p.projectDeadline <= :endDate)
        """
    )
    Page<Project> findBrowseProjectsOrderByRecommended(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("projectType") ProjectType projectType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
    select p
    from Project p
    join fetch p.company
    where p.id = :projectId
    and p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
""")
    Optional<Project> findRecruitingProjectById(
            @Param("projectId") Long projectId
    );

    @Query("""
    select count(p)
    from Project p
    where p.status <> com.conx.server.project.domain.enums.ProjectStatus.DONE
    and p.selectedCrew = :crew
    """)
    int countActiveProjectBySelectedCrew(
            @Param("crew") Crew crew
    );

    @Query("""
    select count(p)
    from Project p
    where p.status = com.conx.server.project.domain.enums.ProjectStatus.DONE
    and p.selectedCrew = :crew
    """)
    int countFinishedProjectBySelectedCrew(
            @Param("crew") Crew crew
    );

    @Query("""
    select p
    from Project p
    where p.selectedCrew = :crew
    and (
        p.status = com.conx.server.project.domain.enums.ProjectStatus.WAITING_RESULT
        or p.status = com.conx.server.project.domain.enums.ProjectStatus.ADJUSTING
    )
    order by p.projectDeadline
    """)
    List<Project> findByTodoProjectCrew(
            @Param("crew") Crew crew
    );

    Optional<Project> findBySelectedCrewAndStatus(Crew selectedCrew, ProjectStatus status);

    Optional<Project> findBySelectedCrewAndId(Crew selectedCrew, long id);

    Page<Project> findAllBySelectedCrewOrderByIdDesc(Crew selectedCrew, Pageable pageable);

    Page<Project> findAllBySelectedCrewAndStatusOrderByIdDesc(
            Crew selectedCrew,
            ProjectStatus status,
            Pageable pageable
    );
}