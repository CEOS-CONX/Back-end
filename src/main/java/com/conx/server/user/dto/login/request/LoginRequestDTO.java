package com.conx.server.user.dto.login.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO(

        @NotBlank(message = "이메일을 입력해주세요")
        @Email
        String email,

        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자로 입력해주세요."
        )
        String password
) {
}
