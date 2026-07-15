package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;

import java.time.LocalDate;

public record CrewPaymentStatusUpdateResponse(
        long settlementId,
        long projectId,
        CrewPaymentStatus paymentStatus,
        LocalDate paymentConfirmedDate,
        ProjectSettlementStatus settlementStatus,
        LocalDate settlementDate
) {

    public static CrewPaymentStatusUpdateResponse from(
            ProjectSettlement settlement
    ) {
        return new CrewPaymentStatusUpdateResponse(
                settlement.getId(),
                settlement.getProject().getId(),
                settlement.getResolvedCrewPaymentStatus(),
                settlement.getCrewPaymentConfirmedDate(),
                settlement.getStatus(),
                settlement.getSettlementDate()
        );
    }
}