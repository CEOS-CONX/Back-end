package com.conx.server.user.dto.emailKey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckingVerificationKeyRequestDTO(
        @NotBlank(message = "이메일을 입력해주세요")
        String email,

        @NotBlank(message = "인증번호를 입력해주세요")
        Integer code
) {

}
