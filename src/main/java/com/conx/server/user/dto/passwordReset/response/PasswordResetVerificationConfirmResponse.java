package com.conx.server.user.dto.passwordReset.response;

public record PasswordResetVerificationConfirmResponse(
        String resetToken
) {
}