package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record CompanySettlementCompleteResponse(
        Long settlementId,
        Long projectId,
        ProjectSettlementStatus settlementStatus,
        ProjectStatus projectStatus,
        LocalDate settlementDate
) {

    public static CompanySettlementCompleteResponse from(
            ProjectSettlement settlement
    ) {
        Project project = settlement.getProject();

        return new CompanySettlementCompleteResponse(
                settlement.getId(),
                project.getId(),
                settlement.getStatus(),
                project.getStatus(),
                settlement.getSettlementDate()
        );
    }
}