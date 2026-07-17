package com.conx.server.user.dto.crew.response;

import java.time.LocalDate;

public record CrewSettlementSummaryResponse(
        long totalPaidAmount,
        long waitingAmount,
        long monthlyPaidAmount,
        LocalDate nextExpectedPaymentDate
) {
}