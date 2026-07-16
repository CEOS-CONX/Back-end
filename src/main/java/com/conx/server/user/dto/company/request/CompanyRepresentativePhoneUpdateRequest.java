package com.conx.server.user.dto.company.request;

public record CompanyRepresentativePhoneUpdateRequest(
        String currentPassword,
        String representativePhone
) {
}