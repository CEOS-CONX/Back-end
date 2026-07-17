package com.conx.server.user.dto.passwordReset.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetVerificationSendRequest(

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotBlank(message = "가입 이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {
}