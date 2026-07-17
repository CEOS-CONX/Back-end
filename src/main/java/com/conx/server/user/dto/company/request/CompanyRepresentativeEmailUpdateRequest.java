package com.conx.server.user.dto.company.request;

public record CompanyRepresentativeEmailUpdateRequest(
        String currentPassword,
        String representativeEmail
) {
}