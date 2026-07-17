package com.conx.server.user.dto.company.request;

public record CompanyNameUpdateRequest(
        String currentPassword,
        String name
) {
}