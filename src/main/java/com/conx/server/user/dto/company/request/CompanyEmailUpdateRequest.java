package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyEmailUpdateRequest(

        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,

        @NotBlank(message = "새 이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String newEmail,

        @NotBlank(message = "이메일 인증 토큰이 필요합니다.")
        String verificationToken
) {
}