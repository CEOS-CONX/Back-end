package com.conx.server.user.repository;

import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    boolean existsByEmail(String email);

    Optional<Crew> findByEmail(String email);

    Optional<Crew> findByEmailAndStatus(String email, UserStatus status);

    Optional<Crew> findByIdAndStatus(Long id, UserStatus status);

    boolean existsByEmailAndStatus(String email, UserStatus status);

    @Query("""
        select new com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO(
            c.id,
            c.profileImage,
            c.crewName,
            c.crewIntroduction,
            c.interestingIndustry,
            c.crewType,
            e.mean,
            c.totalSubsidy
        )
        from Evaluation e
        join e.crew c
        where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
        and c.interestingIndustry = :category
        order by e.mean
    """)
    List<CrewWrapperForLandingPageDTO> findActiveCrewsByCategoryWithEvaluation(
            @Param("category") Industry category
    );

    @Query("""
        select new com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO(
            c.id,
            c.profileImage,
            c.crewName,
            c.crewIntroduction,
            c.interestingIndustry,
            c.crewType,
            e.mean,
            c.totalSubsidy
        )
        from Evaluation e
        join e.crew c
        where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
        order by e.mean
    """)
    List<CrewWrapperForLandingPageDTO> findAllActiveCrewsWithEvaluation();

    @Query("""
    select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
        c.id,
        c.profileImage,
        c.crewName,
        c.crewIntroduction,
        c.interestingIndustry,
        c.crewType,
        coalesce(e.mean, 0),
        c.totalSubsidy
    )
    from Crew c
    left join Evaluation e on e.crew = c
    where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
    and (:keyword is null or c.crewName like concat('%', :keyword, '%')
        or c.crewIntroduction like concat('%', :keyword, '%')
        or c.crewSchool like concat('%', :keyword, '%'))
    and (:category is null or c.interestingIndustry = :category)
    and (:crewType is null or c.crewType = :crewType)
    order by c.id desc
""")
    List<CrewBrowseResponse> findBrowseCrewsOrderByRecent(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType
    );

    @Query("""
    select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
        c.id,
        c.profileImage,
        c.crewName,
        c.crewIntroduction,
        c.interestingIndustry,
        c.crewType,
        coalesce(e.mean, 0),
        c.totalSubsidy
    )
    from Crew c
    left join Evaluation e on e.crew = c
    where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
    and (:keyword is null or c.crewName like concat('%', :keyword, '%')
        or c.crewIntroduction like concat('%', :keyword, '%')
        or c.crewSchool like concat('%', :keyword, '%'))
    and (:category is null or c.interestingIndustry = :category)
    and (:crewType is null or c.crewType = :crewType)
    order by c.totalSubsidy desc, c.id desc
""")
    List<CrewBrowseResponse> findBrowseCrewsOrderByPopular(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType
    );

    @Query("""
    select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
        c.id,
        c.profileImage,
        c.crewName,
        c.crewIntroduction,
        c.interestingIndustry,
        c.crewType,
        coalesce(e.mean, 0),
        c.totalSubsidy
    )
    from Crew c
    left join Evaluation e on e.crew = c
    where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
    and (:keyword is null or c.crewName like concat('%', :keyword, '%')
        or c.crewIntroduction like concat('%', :keyword, '%')
        or c.crewSchool like concat('%', :keyword, '%'))
    and (:category is null or c.interestingIndustry = :category)
    and (:crewType is null or c.crewType = :crewType)
    order by coalesce(e.mean, 0) desc, c.id desc
""")
    List<CrewBrowseResponse> findBrowseCrewsOrderByRating(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType
    );

    @Query("""
    select new com.conx.server.user.dto.crew.response.CrewBrowseResponse(
        c.id,
        c.profileImage,
        c.crewName,
        c.crewIntroduction,
        c.interestingIndustry,
        c.crewType,
        coalesce(e.mean, 0),
        c.totalSubsidy
    )
    from Crew c
    left join Evaluation e on e.crew = c
    where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
    and (:keyword is null or c.crewName like concat('%', :keyword, '%')
        or c.crewIntroduction like concat('%', :keyword, '%')
        or c.crewSchool like concat('%', :keyword, '%'))
    and (:category is null or c.interestingIndustry = :category)
    and (:crewType is null or c.crewType = :crewType)
    order by coalesce(e.mean, 0) desc, c.totalSubsidy desc, c.id desc
""")
    List<CrewBrowseResponse> findBrowseCrewsOrderByRecommended(
            @Param("keyword") String keyword,
            @Param("category") Industry category,
            @Param("crewType") CrewType crewType
    );

    @Query("""
    select coalesce(e.mean, 0)
    from Evaluation e
    where e.crew.id = :crewId
""")
    Optional<Double> findEvaluationMeanByCrewId(@Param("crewId") Long crewId);

    String email(String email);

}