package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.dto.company.response.CompanyExpenditureStatusResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectSettlementRepository extends JpaRepository<ProjectSettlement, Long> {

    boolean existsByProjectId(Long projectId);

    List<ProjectSettlement> findAllByCompanyIdOrderByIdDesc(Long companyId);

    List<ProjectSettlement> findAllByCompanyIdAndStatusOrderByIdDesc(
            Long companyId,
            ProjectSettlementStatus status
    );

    Optional<ProjectSettlement> findByIdAndCompanyId(Long settlementId, Long companyId);

    Optional<ProjectSettlement> findByProjectIdAndCrewId(Long projectId, Long crewId);

    Page<ProjectSettlement> findAllByCrewIdOrderByIdDesc(Long crewId, Pageable pageable);

    Page<ProjectSettlement> findAllByCrewIdAndStatusOrderByIdDesc(
            Long crewId,
            ProjectSettlementStatus status,
            Pageable pageable
    );

    @Query("""
    select new com.conx.server.user.dto.company.response.CompanyExpenditureStatusResponseDTO(
        count(case when function('MONTH', s.paymentDate) = 1 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 2 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 3 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 4 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 5 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 6 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 7 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 8 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 9 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 10 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 11 then 1 end),
        count(case when function('MONTH', s.paymentDate) = 12 then 1 end),
        c.totalExpenditure
    )
    from ProjectSettlement s
    join s.project p
    join p.company c
    where c = :company
      and function('YEAR', s.paymentDate) = :year
    group by c.totalExpenditure
""")
    CompanyExpenditureStatusResponseDTO findCompanyStatusWithCompany(
            @Param("company") Company company,
            @Param("year") int year
    );


    @Query("""
    select s from ProjectSettlement s
    join s.project p
    join p.company c
    where c = :company
      and (:status is null or s.status = :status)
      and (:startDate is null or s.paymentDate >= :startDate)
      and (:endDate is null or s.paymentDate <= :endDate)
""")
    Page<ProjectSettlement> findByCompanyAndFilters(
            @Param("company") Company company,
            @Param("status") ProjectSettlementStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}