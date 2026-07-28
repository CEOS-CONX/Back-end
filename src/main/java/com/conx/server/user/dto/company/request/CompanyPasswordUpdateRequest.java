package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.NotBlank;

public record CompanyPasswordUpdateRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword,
        @NotBlank
        String newPasswordConfirmation
) {
}