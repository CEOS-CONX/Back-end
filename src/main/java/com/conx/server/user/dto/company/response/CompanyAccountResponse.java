package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.company.Company;

public record CompanyAccountResponse(
        String email,
        String companyName,
        String businessRegistrationNumber,
        String managerName,
        String job
) {

    public static CompanyAccountResponse from(Company company) {
        return new CompanyAccountResponse(
                company.getEmail(),
                company.getCompanyName(),
                company.getBusinessRegistrationNumber(),
                company.getManagerName(),
                company.getJob()
        );
    }
}