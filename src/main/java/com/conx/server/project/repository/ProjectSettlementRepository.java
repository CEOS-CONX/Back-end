package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.response.CompanyExpenditureStatusResponseDTO;
import com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectSettlementRepository
        extends JpaRepository<ProjectSettlement, Long> {

    boolean existsByProjectId(
            Long projectId
    );

    long countByCrewAndStatus(
            Crew crew,
            ProjectSettlementStatus status
    );

    /**
     * 메서드명은 기존 서비스 호환을 위해 유지하고,
     * 실제 엔티티 필드인 subsidy를 합산한다.
     */
    @Query("""
            select coalesce(sum(settlement.subsidy), 0)
            from ProjectSettlement settlement
            where settlement.crew = :crew
              and settlement.status = :status
            """)
    long sumAmountByCrewAndStatus(
            @Param("crew")
            Crew crew,

            @Param("status")
            ProjectSettlementStatus status
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

    /**
     * 기업이 정산 상태를 변경할 때 사용하는 잠금 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select settlement
            from ProjectSettlement settlement
            join fetch settlement.project project
            where settlement.id = :settlementId
              and settlement.company.id = :companyId
            """)
    Optional<ProjectSettlement> findByIdAndCompanyIdForUpdate(
            @Param("settlementId")
            Long settlementId,

            @Param("companyId")
            Long companyId
    );

    /**
     * 크루가 자신의 지급 확인 상태를 변경할 때 사용하는 잠금 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select settlement
            from ProjectSettlement settlement
            join fetch settlement.project project
            where settlement.id = :settlementId
              and settlement.crew.id = :crewId
            """)
    Optional<ProjectSettlement> findByIdAndCrewIdForUpdate(
            @Param("settlementId")
            Long settlementId,

            @Param("crewId")
            Long crewId
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

    /**
     * 기업의 연도별 월간 정산 현황
     *
     * dev에서 추가된 기업 정산 관리 기능을 유지한다.
     */
    @Query("""
            select new com.conx.server.user.dto.company.response.CompanyExpenditureStatusResponseDTO(
                count(case when function('MONTH', settlement.paymentDate) = 1 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 2 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 3 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 4 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 5 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 6 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 7 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 8 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 9 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 10 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 11 then 1 end),
                count(case when function('MONTH', settlement.paymentDate) = 12 then 1 end),
                company.totalExpenditure
            )
            from ProjectSettlement settlement
            join settlement.company company
            where company = :company
              and function('YEAR', settlement.paymentDate) = :year
            group by company.totalExpenditure
            """)
    CompanyExpenditureStatusResponseDTO findCompanyStatusWithCompany(
            @Param("company")
            Company company,

            @Param("year")
            int year
    );

    /**
     * 기업 정산 목록 필터 조회
     */
    @Query(
            value = """
                    select settlement
                    from ProjectSettlement settlement
                    join fetch settlement.project project
                    where settlement.company = :company
                      and (:status is null or settlement.status = :status)
                      and (:startDate is null or settlement.paymentDate >= :startDate)
                      and (:endDate is null or settlement.paymentDate <= :endDate)
                    order by settlement.id desc
                    """,
            countQuery = """
                    select count(settlement)
                    from ProjectSettlement settlement
                    where settlement.company = :company
                      and (:status is null or settlement.status = :status)
                      and (:startDate is null or settlement.paymentDate >= :startDate)
                      and (:endDate is null or settlement.paymentDate <= :endDate)
                    """
    )
    Page<ProjectSettlement> findByCompanyAndFilters(
            @Param("company")
            Company company,

            @Param("status")
            ProjectSettlementStatus status,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate,

            Pageable pageable
    );

    /**
     * 크루 정산 요약
     *
     * 실제 정산 상태와 실제 정산일만 집계 기준으로 사용한다.
     * 크루 지급 확인 상태는 집계에 영향을 주지 않는다.
     */
    @Query("""
            select new com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse(
                coalesce(
                    sum(
                        case
                            when settlement.status =
                                com.conx.server.project.domain.enums.ProjectSettlementStatus.PAID
                            then settlement.subsidy
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
                            then settlement.subsidy
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
                                and settlement.paymentDate >= :monthStart
                                and settlement.paymentDate <= :monthEnd
                            then settlement.subsidy
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
     * startDate, endDate:
     * 프로젝트 수행 기간 필터
     *
     * settlementStartDate, settlementEndDate:
     * 실제 CONX 정산 완료일 필터
     *
     * crewPaymentStatus:
     * 크루가 직접 선택한 지급 확인 상태
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
                            or project.projectName like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                            or company.companyName like concat('%', :keyword, '%')
                      )
                      and (
                            :settlementStatus is null
                            or settlement.status = :settlementStatus
                      )
                      and (
                            :crewPaymentStatus is null
                            or settlement.crewPaymentStatus = :crewPaymentStatus
                            or (
                                settlement.crewPaymentStatus is null
                                and :crewPaymentStatus =
                                    com.conx.server.project.domain.enums.CrewPaymentStatus.BEFORE_PAYMENT
                            )
                      )
                      and (
                            :category is null
                            or project.industry = :category
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
                      and (
                            :settlementStartDate is null
                            or settlement.paymentDate >= :settlementStartDate
                      )
                      and (
                            :settlementEndDate is null
                            or settlement.paymentDate <= :settlementEndDate
                      )
                    order by
                      case
                          when settlement.paymentDate is null
                          then 1
                          else 0
                      end asc,
                      case
                          when :sort = 'RECENT'
                          then settlement.paymentDate
                          else null
                      end desc,
                      case
                          when :sort = 'OLDEST'
                          then settlement.paymentDate
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
                            or project.projectName like concat('%', :keyword, '%')
                            or project.brandName like concat('%', :keyword, '%')
                            or company.companyName like concat('%', :keyword, '%')
                      )
                      and (
                            :settlementStatus is null
                            or settlement.status = :settlementStatus
                      )
                      and (
                            :crewPaymentStatus is null
                            or settlement.crewPaymentStatus = :crewPaymentStatus
                            or (
                                settlement.crewPaymentStatus is null
                                and :crewPaymentStatus =
                                    com.conx.server.project.domain.enums.CrewPaymentStatus.BEFORE_PAYMENT
                            )
                      )
                      and (
                            :category is null
                            or project.industry = :category
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
                      and (
                            :settlementStartDate is null
                            or settlement.paymentDate >= :settlementStartDate
                      )
                      and (
                            :settlementEndDate is null
                            or settlement.paymentDate <= :settlementEndDate
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

            @Param("crewPaymentStatus")
            CrewPaymentStatus crewPaymentStatus,

            @Param("category")
            Industry category,

            @Param("projectType")
            ProjectType projectType,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate,

            @Param("settlementStartDate")
            LocalDate settlementStartDate,

            @Param("settlementEndDate")
            LocalDate settlementEndDate,

            @Param("sort")
            String sort,

            Pageable pageable
    );
}
