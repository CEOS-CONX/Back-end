package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.user.domain.crew.Crew;

import java.time.LocalDate;

public record CompanySettlementResponse(
        Long settlementId,
        Long projectId,
        String projectName,
        ProjectStatus projectStatus,
        Long crewId,
        String crewName,
        long amount,
        ProjectSettlementStatus settlementStatus,
        LocalDate expectedPaymentDate
) {

    public static CompanySettlementResponse from(ProjectSettlement settlement) {
        Project project = settlement.getProject();
        Crew crew = settlement.getCrew();

        return new CompanySettlementResponse(
                settlement.getId(),
                project.getId(),
                project.getProjectName(),
                project.getStatus(),
                crew.getId(),
                crew.getCrewName(),
                settlement.getSubsidy(),
                settlement.getStatus(),
                settlement.getExpectedPaymentDate()
        );
    }
}