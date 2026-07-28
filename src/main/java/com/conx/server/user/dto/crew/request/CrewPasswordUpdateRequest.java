package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;

public record CrewPasswordUpdateRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword,
        @NotBlank
        String newPasswordConfirmation
) {
}
