package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CompanySettlementExpectedPaymentDateRequest(
        @NotNull(message = "예상 지급날짜를 입력해주세요")
        LocalDate expectedPaymentDate
) {
}