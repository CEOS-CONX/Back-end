package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CrewSettlementItemResponse(
        long settlementId,
        long projectId,
        String projectName,
        String brandName,
        String companyName,
        Industry category,
        ProjectType projectType,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        long amount,
        ProjectSettlementStatus settlementStatus,
        LocalDate expectedPaymentDate,
        LocalDate settlementDate,
        LocalDateTime registeredAt
) {

    public static CrewSettlementItemResponse from(
            ProjectSettlement settlement
    ) {
        Project project =
                settlement.getProject();

        Company company =
                project.getCompany();

        return new CrewSettlementItemResponse(
                settlement.getId(),
                project.getId(),
                project.getName(),
                project.getBrandName(),
                company.getCompanyName(),
                company.getIndustry(),
                project.getProjectType(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                settlement.getAmount(),
                settlement.getStatus(),
                settlement.getExpectedPaymentDate(),
                settlement.getSettlementDate(),
                settlement.getCreatedAt()
        );
    }
}