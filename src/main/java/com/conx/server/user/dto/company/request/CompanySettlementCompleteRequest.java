package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CompanySettlementCompleteRequest(
        @NotNull
        LocalDate settlementDate
) {
}