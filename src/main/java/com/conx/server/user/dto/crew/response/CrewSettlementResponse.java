package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record CrewSettlementResponse(
        Long settlementId,
        Long projectId,
        String projectName,
        String brandName,
        ProjectStatus projectStatus,
        long amount,
        ProjectSettlementStatus settlementStatus,
        LocalDate expectedPaymentDate
) {
    public static CrewSettlementResponse from(ProjectSettlement settlement) {
        Project project = settlement.getProject();

        return new CrewSettlementResponse(
                settlement.getId(),
                project.getId(),
                project.getProjectName(),
                project.getBrandName(),
                project.getStatus(),
                settlement.getSubsidy(),
                settlement.getStatus(),
                settlement.getExpectedPaymentDate()
        );
    }
}