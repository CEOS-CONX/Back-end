package com.conx.server.user.dto.emailKey;

import jakarta.validation.constraints.NotBlank;

public record SendingVerificationKeyRequestDTO(
        @NotBlank(message = "이메일을 입력해주세요")
        String email
) {
}
