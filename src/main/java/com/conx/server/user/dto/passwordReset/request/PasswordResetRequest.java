package com.conx.server.user.dto.passwordReset.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(

        @NotBlank(message = "비밀번호 재설정 토큰이 필요합니다.")
        String resetToken,

        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        String newPassword,

        @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
        String newPasswordConfirmation
) {
}