package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;

import java.time.LocalDate;

public record CompanySettlementExpectedPaymentDateResponse(
        Long settlementId,
        Long projectId,
        ProjectSettlementStatus settlementStatus,
        LocalDate expectedPaymentDate
) {

    public static CompanySettlementExpectedPaymentDateResponse from(ProjectSettlement settlement) {
        return new CompanySettlementExpectedPaymentDateResponse(
                settlement.getId(),
                settlement.getProject().getId(),
                settlement.getStatus(),
                settlement.getExpectedPaymentDate()
        );
    }
}