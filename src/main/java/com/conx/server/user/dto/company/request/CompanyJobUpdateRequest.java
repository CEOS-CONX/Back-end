package com.conx.server.user.dto.company.request;

public record CompanyJobUpdateRequest(
        String currentPassword,
        String job
) {
}