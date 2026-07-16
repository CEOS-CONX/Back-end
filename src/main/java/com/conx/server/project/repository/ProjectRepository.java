package com.conx.server.project.repository;

import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.response.TodoProjectInfoDTO;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.dto.company.response.CompanyProjectStatusResponseDTO;
import com.conx.server.user.dto.company.response.CompanyTodoProjectResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndCompanyId(Long projectId, Long companyId);

    Optional<Project> findByIdAndCompanyIdAndStatus(Long projectId, Long companyId, ProjectStatus status);

    @Query("""
    select p
    from Project p
    where p.company.id = :companyId
    and p.status <> com.conx.server.project.domain.enums.ProjectStatus.DRAFT
    and (:keyword is null or p.projectName like concat('%', :keyword, '%'))
    and (:crewType is null or p.crewType = :crewType)
    and (:startDate is null or p.projectStartDate >= :startDate)
    and (:endDate is null or p.projectDeadline <= :endDate)
    and (:category is null or p.industry = :category)
""")
    Page<Project> findCompanyProjectsByFilter(
            @Param("companyId") Long companyId,
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType,
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
        and (:keyword is null or p.projectName like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.recruitDeadLine >= :startDate)
        and (:endDate is null or p.recruitDeadLine <= :endDate)
        order by p.id desc
        """,
            countQuery = """
        select count(p)
        from Project p
        join p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.projectName like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.recruitDeadLine >= :startDate)
        and (:endDate is null or p.recruitDeadLine <= :endDate)
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
        and (:keyword is null or p.projectName like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.recruitDeadLine >= :startDate)
        and (:endDate is null or p.recruitDeadLine <= :endDate)
        order by p.views desc, p.id desc
        """,
            countQuery = """
        select count(p)
        from Project p
        join p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.projectName like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.recruitDeadLine >= :startDate)
        and (:endDate is null or p.recruitDeadLine <= :endDate)
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
        and (:keyword is null or p.projectName like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.recruitDeadLine >= :startDate)
        and (:endDate is null or p.recruitDeadLine <= :endDate)
        order by p.views desc, p.id desc
        """,
            countQuery = """
        select count(p)
        from Project p
        join p.company c
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and (:keyword is null or p.projectName like concat('%', :keyword, '%')
            or c.companyName like concat('%', :keyword, '%')
            or p.brandName like concat('%', :keyword, '%'))
        and (:category is null or c.industry = :category)
        and (:projectType is null or p.projectType = :projectType)
        and (:startDate is null or p.recruitDeadLine >= :startDate)
        and (:endDate is null or p.recruitDeadLine <= :endDate)
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

    Optional<Project> findBySelectedCrewAndId(Crew selectedCrew, long id);

    @Query("""
        select p
        from Project p
        where p.recruitDeadLine = :now
    """)
    List<Project> findExpireProject(
            @Param("now") LocalDate now
    );

    @Query("""
        select p
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.WAITING_RESULT
        and p.submitDeadline in :deadline
    """)
    List<Project> findAllAboutSubmitDeadlineProject(
            @Param("deadline") List<LocalDate> deadline
    );

    @Query("""
        select p
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.PROGRESS
        and p.projectDeadline in :deadline
    """)
    List<Project> findAllAboutProjectDeadlineProject(
            @Param("deadline") List<LocalDate> deadline
    );

    @Query("""
        select p from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and p.projectDeadline in :deadlines
    """)
    List<Project> findAllAboutRecruitingDeadline(
            @Param("deadlines") List<LocalDate> deadlines
    );

    @Query("""
        select p
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.WAITING_RESULT
        and p.submitDeadline > :now
    """)
    List<Project> findAllAboutLateProject(
            @Param("now") LocalDate now
    );

    List<Project> findAllByProjectDeadlineAndStatus(LocalDate projectDeadline, ProjectStatus status);

    List<Project> findAllBySelectedCrew(Crew selectedCrew);

    @Query("""
        select new com.conx.server.user.dto.company.response.CompanyProjectStatusResponseDTO(
            count(case when p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING then 1 end),
            count(case when p.status in (
                com.conx.server.project.domain.enums.ProjectStatus.PROGRESS,
                com.conx.server.project.domain.enums.ProjectStatus.CONTRACT_PENDING
            ) then 1 end),
            count(case when p.status = com.conx.server.project.domain.enums.ProjectStatus.INSPECTION then 1 end),
            count(case when p.status = com.conx.server.project.domain.enums.ProjectStatus.ADJUSTING then 1 end),
            count(case when p.status = com.conx.server.project.domain.enums.ProjectStatus.DONE then 1 end)
        )
        from Project p
        where p.company = :company
    """)
    CompanyProjectStatusResponseDTO findCompanyStatusWithCompany(
            @Param("company") Company company
    );

    @Query("""
    select p from Project p
    where p.company = :company
    and (:status is null or p.status = :status)
    and (:startDate is null or p.createdAt >= :startDate)
    and (:endDate is null or p.createdAt <= :endDate)
""")
    Page<Project> findByCompanyWithFilters(
            @Param("company") Company company,
            @Param("status") ProjectStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    List<Project> findAllByCompany(Company company);

    List<Project> findAllOrderByViews(int views);
}
//int recruiting,
//        int progress,
//        int waiting_inspection,
//        int waiting_settlement,
//        int done