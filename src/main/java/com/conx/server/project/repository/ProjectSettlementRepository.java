package com.conx.server.project.repository;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}