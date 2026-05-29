package com.conx.server.user.dto.company.request;

import java.time.LocalDate;

public record CompanySettlementExpectedPaymentDateRequest(
        LocalDate expectedPaymentDate
) {
}