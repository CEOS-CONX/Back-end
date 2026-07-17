package com.conx.server.user.dto.company.request;

public record CompanyPasswordUpdateRequest(
        String currentPassword,
        String newPassword,
        String newPasswordConfirmation
) {
}