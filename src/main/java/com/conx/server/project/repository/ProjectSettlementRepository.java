package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse;
import jakarta.persistence.LockModeType;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.time.LocalDate;
import org.springframework.data.repository.query.Param;

public interface ProjectSettlementRepository
        extends JpaRepository<ProjectSettlement, Long> {

    boolean existsByProjectId(Long projectId);

    long countByCrewAndStatus(
            Crew crew,
            ProjectSettlementStatus status
    );

    @Query("""
        select coalesce(sum(settlement.amount), 0)
        from ProjectSettlement settlement
        where settlement.crew = :crew
          and settlement.status = :status
        """)
    long sumAmountByCrewAndStatus(
            @Param("crew") Crew crew,
            @Param("status") ProjectSettlementStatus status
    );

    List<ProjectSettlement> findAllByCompanyIdOrderByIdDesc(
            Long companyId
    );

    List<ProjectSettlement> findAllByCompanyIdAndStatusOrderByIdDesc(
            Long companyId,
            ProjectSettlementStatus status
    );

    Optional<ProjectSettlement> findByIdAndCompanyId(
            Long settlementId,
            Long companyId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select settlement
            from ProjectSettlement settlement
            where settlement.id = :settlementId
              and settlement.company.id = :companyId
            """)
    Optional<ProjectSettlement> findByIdAndCompanyIdForUpdate(
            @Param("settlementId") Long settlementId,
            @Param("companyId") Long companyId
    );

    Optional<ProjectSettlement> findByProjectIdAndCrewId(
            Long projectId,
            Long crewId
    );

    Page<ProjectSettlement> findAllByCrewIdOrderByIdDesc(
            Long crewId,
            Pageable pageable
    );

    Page<ProjectSettlement> findAllByCrewIdAndStatusOrderByIdDesc(
            Long crewId,
            ProjectSettlementStatus status,
            Pageable pageable
    );

    @Query("""
        select new com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse(
            coalesce(
                sum(
                    case
                        when settlement.status =
                            com.conx.server.project.domain.enums.ProjectSettlementStatus.PAID
                        then settlement.amount
                        else 0L
                    end
                ),
                0L
            ),
            coalesce(
                sum(
                    case
                        when settlement.status =
                            com.conx.server.project.domain.enums.ProjectSettlementStatus.WAITING
                        then settlement.amount
                        else 0L
                    end
                ),
                0L
            ),
            coalesce(
                sum(
                    case
                        when settlement.status =
                            com.conx.server.project.domain.enums.ProjectSettlementStatus.PAID
                            and settlement.settlementDate >= :monthStart
                            and settlement.settlementDate <= :monthEnd
                        then settlement.amount
                        else 0L
                    end
                ),
                0L
            ),
            min(
                case
                    when settlement.status =
                        com.conx.server.project.domain.enums.ProjectSettlementStatus.WAITING
                    then settlement.expectedPaymentDate
                    else null
                end
            )
        )
        from ProjectSettlement settlement
        where settlement.crew.id = :crewId
        """)
    CrewSettlementSummaryResponse findCrewSettlementSummary(
            @Param("crewId")
            Long crewId,

            @Param("monthStart")
            LocalDate monthStart,

            @Param("monthEnd")
            LocalDate monthEnd
    );

    /**
     * 크루 정산 목록 검색 및 필터 조회
     *
     * 수행 기간 필터는 선택한 기간과 프로젝트 수행 기간이
     * 조금이라도 겹치는 프로젝트를 조회한다.
     *
     * 정산일이 없는 WAITING 정산은 실제 지급일 정렬에서 뒤로 배치한다.
     */
    @Query(
            value = """
                select settlement
                from ProjectSettlement settlement
                join fetch settlement.project project
                join fetch project.company company
                where settlement.crew.id = :crewId
                  and (
                        :keyword is null
                        or project.name like concat('%', :keyword, '%')
                        or project.brandName like concat('%', :keyword, '%')
                        or company.companyName like concat('%', :keyword, '%')
                  )
                  and (
                        :settlementStatus is null
                        or settlement.status = :settlementStatus
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
                        or project.projectDeadline >= :startDate
                  )
                  and (
                        :endDate is null
                        or project.projectStartDate <= :endDate
                  )
                order by
                  case
                      when settlement.settlementDate is null
                      then 1
                      else 0
                  end asc,
                  case
                      when :sort = 'RECENT'
                      then settlement.settlementDate
                      else null
                  end desc,
                  case
                      when :sort = 'OLDEST'
                      then settlement.settlementDate
                      else null
                  end asc,
                  case
                      when :sort = 'RECENT'
                      then settlement.id
                      else null
                  end desc,
                  case
                      when :sort = 'OLDEST'
                      then settlement.id
                      else null
                  end asc
                """,
            countQuery = """
                select count(settlement)
                from ProjectSettlement settlement
                join settlement.project project
                join project.company company
                where settlement.crew.id = :crewId
                  and (
                        :keyword is null
                        or project.name like concat('%', :keyword, '%')
                        or project.brandName like concat('%', :keyword, '%')
                        or company.companyName like concat('%', :keyword, '%')
                  )
                  and (
                        :settlementStatus is null
                        or settlement.status = :settlementStatus
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
                        or project.projectDeadline >= :startDate
                  )
                  and (
                        :endDate is null
                        or project.projectStartDate <= :endDate
                  )
                """
    )
    Page<ProjectSettlement> findCrewSettlements(
            @Param("crewId")
            Long crewId,

            @Param("keyword")
            String keyword,

            @Param("settlementStatus")
            ProjectSettlementStatus settlementStatus,

            @Param("category")
            Industry category,

            @Param("projectType")
            ProjectType projectType,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate,

            @Param("sort")
            String sort,

            Pageable pageable
    );
}