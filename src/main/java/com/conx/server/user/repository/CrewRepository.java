package com.conx.server.user.repository;

import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CrewRepository
        extends JpaRepository<Crew, Long> {

    boolean existsByEmail(String email);

    Optional<Crew> findByEmail(String email);

    Optional<Crew> findByEmailAndStatus(
            String email,
            UserStatus status
    );

    Optional<Crew> findByIdAndStatus(
            Long id,
            UserStatus status
    );

    boolean existsByEmailAndStatus(
            String email,
            UserStatus status
    );

    @Query(
            value = """
            select p
            from Project p
            where p.company.id = :companyId
              and p.selectedCrew is not null
              and (
                    :keyword is null
                    or p.selectedCrew.crewName like concat('%', :keyword, '%')
              )
              and (:statuses is null or p.status in :statuses)
              and (:category is null or p.selectedCrew.interestingIndustry = :category)
              and (:crewType is null or p.selectedCrew.crewType = :crewType)
              and (:startDate is null or p.projectStartDate >= :startDate)
              and (:endDate is null or p.projectDeadline <= :endDate)
            order by p.id desc
            """,
            countQuery = """
            select count(p)
            from Project p
            where p.company.id = :companyId
              and p.selectedCrew is not null
              and (
                    :keyword is null
                    or p.selectedCrew.crewName like concat('%', :keyword, '%')
              )
              and (:statuses is null or p.status in :statuses)
              and (:category is null or p.selectedCrew.interestingIndustry = :category)
              and (:crewType is null or p.selectedCrew.crewType = :crewType)
              and (:startDate is null or p.projectStartDate >= :startDate)
              and (:endDate is null or p.projectDeadline <= :endDate)
            """
    )
    Page<Project> findPartnerCrewProjectsByFilter(
            @Param("companyId") Long companyId,
            @Param("keyword") String keyword,
            @Param("statuses") List<ProjectStatus> statuses,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
    select new com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO(
        c.id,
        c.profileImage,
        c.crewName,
        c.crewIntroduction,
        c.interestingIndustry,
        c.crewType,
        coalesce(avg(e.mean), 0.0),
        c.totalProjectCount
    )
    from Crew c
    left join Evaluation e on e.crew = c
    where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
    group by
        c.id,
        c.profileImage,
        c.crewName,
        c.crewIntroduction,
        c.interestingIndustry,
        c.crewType,
        c.totalSubsidy,
        c.totalProjectCount
    order by
        coalesce(avg(e.mean), 0.0) desc,
        c.totalProjectCount desc,
        c.crewName asc
""")
    List<CrewWrapperForLandingPageDTO>
    findAllActiveCrewsWithEvaluation();

    @Query(
            value = """
                select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    coalesce(avg(e.mean), 0.0),
                    c.totalProjectCount
                )
                from Crew c
                left join Evaluation e on e.crew = c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                group by
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    c.totalProjectCount
                order by c.id desc
                """,
            countQuery = """
                select count(c)
                from Crew c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                """
    )
    Page<CrewBrowseResponse> findBrowseCrewsOrderByRecent(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType,
            Pageable pageable
    );

    @Query(
            value = """
                select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    coalesce(avg(e.mean), 0.0),
                    c.totalProjectCount
                )
                from Crew c
                left join Evaluation e on e.crew = c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                group by
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    c.totalProjectCount,
                    c.totalSubsidy
                order by
                    c.totalSubsidy desc,
                    c.id desc
                """,
            countQuery = """
                select count(c)
                from Crew c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                """
    )
    Page<CrewBrowseResponse> findBrowseCrewsOrderByPopular(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType,
            Pageable pageable
    );

    @Query(
            value = """
                select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    coalesce(avg(e.mean), 0.0),
                    c.totalProjectCount
                )
                from Crew c
                left join Evaluation e on e.crew = c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                group by
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    c.totalProjectCount
                order by
                    coalesce(avg(e.mean), 0.0) desc,
                    c.id desc
                """,
            countQuery = """
                select count(c)
                from Crew c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                """
    )
    Page<CrewBrowseResponse> findBrowseCrewsOrderByRating(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType,
            Pageable pageable
    );

    @Query(
            value = """
                select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    coalesce(avg(e.mean), 0.0),
                    c.totalProjectCount
                )
                from Crew c
                left join Evaluation e on e.crew = c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                group by
                    c.id,
                    c.profileImage,
                    c.crewName,
                    c.crewIntroduction,
                    c.interestingIndustry,
                    c.crewType,
                    c.totalProjectCount,
                    c.totalSubsidy
                order by
                    coalesce(avg(e.mean), 0.0) desc,
                    c.totalSubsidy desc,
                    c.id desc
                """,
            countQuery = """
                select count(c)
                from Crew c
                where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
                and (
                    :keyword is null
                    or c.crewName like concat('%', :keyword, '%')
                    or c.crewIntroduction like concat('%', :keyword, '%')
                )
                and (
                    :category is null
                    or c.interestingIndustry = :category
                )
                and (
                    :crewType is null
                    or c.crewType = :crewType
                )
                """
    )
    Page<CrewBrowseResponse> findBrowseCrewsOrderByRecommended(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType,
            Pageable pageable
    );

    @Query("""
        select avg(e.mean)
        from Evaluation e
        where e.crew.id = :crewId
    """)
    Optional<Double> findEvaluationMeanByCrewId(
            @Param("crewId") Long crewId
    );
}