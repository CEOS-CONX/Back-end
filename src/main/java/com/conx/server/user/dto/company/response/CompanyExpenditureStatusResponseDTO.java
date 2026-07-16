package com.conx.server.user.dto.company.response;

public record CompanyExpenditureStatusResponseDTO(
        long jan, long feb, long MRCH, long april, long may, long june, long july, long aug, long sep, long oct, long nov, long dec,
        int expenditure
) {
}
