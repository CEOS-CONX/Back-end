package com.conx.server.user.dto.passwordReset.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordResetVerificationConfirmRequest(

        @NotBlank(message = "가입 이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotNull(message = "인증번호를 입력해주세요.")
        Integer code
) {
}