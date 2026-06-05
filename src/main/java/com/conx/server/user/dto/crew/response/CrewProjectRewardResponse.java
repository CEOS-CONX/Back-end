package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;

import java.time.LocalDate;

public record CrewProjectRewardResponse(
        Long settlementId,
        Long projectId,
        Long amount,
        LocalDate expectedPaymentDate,
        ProjectSettlementStatus status
) {
    public static CrewProjectRewardResponse from(ProjectSettlement settlement) {
        return new CrewProjectRewardResponse(
                settlement.getId(),
                settlement.getProject().getId(),
                settlement.getAmount(),
                settlement.getExpectedPaymentDate(),
                settlement.getStatus()
        );
    }
}