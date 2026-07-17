package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.company.Company;

public record CompanyAccountResponse(
        String name,
        String email,
        String job,
        String representativePhone,
        String representativeEmail
) {

    public static CompanyAccountResponse from(Company company) {
        return new CompanyAccountResponse(
                company.getManagerName(),
                company.getEmail(),
                company.getJob(),
                company.getRepresentativePhone(),
                company.getRepresentativeEmail()
        );
    }
}