package com.conx.server.user.repository;

import com.conx.server.landingPage.dto.CrewWrapperDTOForDashboard;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.domain.types.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    boolean existsByEmail(String email);

    Optional<Crew> findByEmail(String email);

    User findByEmailAndStatus(String email, UserStatus status);

    boolean existsByEmailAndStatus(String email, UserStatus status);

    @Query("""
        select new com.conx.server.landingPage.dto.CrewWrapperDTOForDashboard(
            c.id,
            c.profileImage,
            c.crewName,
            c.crewIntroduction,
            c.interestingIndustry,
            c.crewType,
            e.mean,
            c.cumulative
        )
        from Evaluation e
        join e.crew c
        where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
        and c.interestingIndustry = :category
        order by e.mean
    """)
    List<CrewWrapperDTOForDashboard> findActiveCrewsByCategoryWithEvaluation(
            @Param("category") Industry category
    );

    @Query("""
        select new com.conx.server.landingPage.dto.CrewWrapperDTOForDashboard(
            c.id,
            c.profileImage,
            c.crewName,
            c.crewIntroduction,
            c.interestingIndustry,
            c.crewType,
            e.mean,
            c.cumulative
        )
        from Evaluation e
        join e.crew c
        where c.status = com.conx.server.user.domain.types.UserStatus.ACTIVE
        order by e.mean
    """)
    List<CrewWrapperDTOForDashboard> findAllActiveCrewsWithEvaluation();
}