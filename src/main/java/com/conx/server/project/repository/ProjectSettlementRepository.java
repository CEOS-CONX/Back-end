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
        count(case when s.month = java.time.Month.JANUARY then 1 end),
        count(case when s.month = java.time.Month.FEBRUARY then 1 end),
        count(case when s.month = java.time.Month.MARCH then 1 end),
        count(case when s.month = java.time.Month.APRIL then 1 end),
        count(case when s.month = java.time.Month.MAY then 1 end),
        count(case when s.month = java.time.Month.JUNE then 1 end),
        count(case when s.month = java.time.Month.JULY then 1 end),
        count(case when s.month = java.time.Month.AUGUST then 1 end),
        count(case when s.month = java.time.Month.SEPTEMBER then 1 end),
        count(case when s.month = java.time.Month.OCTOBER then 1 end),
        count(case when s.month = java.time.Month.NOVEMBER then 1 end),
        count(case when s.month = java.time.Month.DECEMBER then 1 end),
        c.totalExpenditure
    )
    from ProjectSettlement s
    join s.project p
    join p.company c
    where c = :company
    group by c.totalExpenditure
""")
    CompanyExpenditureStatusResponseDTO findCompanyStatusWithCompany(
            @Param("company") Company company
    );
}