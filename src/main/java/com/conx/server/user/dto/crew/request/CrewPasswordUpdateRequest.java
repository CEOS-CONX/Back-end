package com.conx.server.user.dto.crew.request;

public record CrewPasswordUpdateRequest(
        String currentPassword,
        String newPassword,
        String newPasswordConfirmation
) {
}
