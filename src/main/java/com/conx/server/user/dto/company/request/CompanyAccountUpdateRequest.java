package com.conx.server.user.dto.company.request;

public record CompanyAccountUpdateRequest(
        String companyName,
        String businessRegistrationNumber,
        String managerName,
        String job
) {
}