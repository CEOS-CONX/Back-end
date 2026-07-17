package com.conx.server.bookmark.repository;

import com.conx.server.bookmark.domain.CrewBookmark;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewBookmarkRepository
        extends JpaRepository<CrewBookmark, Long> {

    Optional<CrewBookmark> findByCompanyIdAndCrewId(
            Long companyId,
            Long crewId
    );

    boolean existsByCompanyIdAndCrewId(
            Long companyId,
            Long crewId
    );

    List<CrewBookmark> findAllByCompanyId(
            Long companyId
    );

    @Query("""
        select b.crew.id
        from CrewBookmark b
        where b.company.id = :companyId
        and b.crew.id in :crewIds
    """)
    List<Long> findBookmarkedCrewIdsByCompanyIdAndCrewIds(
            @Param("companyId") Long companyId,
            @Param("crewIds") List<Long> crewIds
    );

    @Query("""
        select new com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse(
            c.id,
            c.profileImage,
            c.crewName,
            c.crewIntroduction,
            c.crewType,
            c.customCrewType,
            c.interestingIndustry,
            c.memberAmount,
            c.totalSubsidy,
            case
                when e is null or e.evaluationCount = 0 then cast(0.0 as double)
                else cast(
                    (e.completeness + e.schedule + e.ability + e.reCooperation + e.communication) * 1.0
                    / (5 * e.evaluationCount)
                as double)
            end
        )
        from CrewBookmark b
        join b.crew c
        left join Evaluation e on e.crew = c
        where b.company.id = :companyId
    """)
    List<CompanyBookmarkedCrewResponse>
    findAllBookmarkedCrewResponsesByCompanyId(
            @Param("companyId") Long companyId
    );
}