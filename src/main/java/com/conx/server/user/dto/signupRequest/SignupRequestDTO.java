package com.conx.server.user.dto.signupRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignupRequestDTO(
        @NotNull @Email String email,
        @NotNull String password,
        @NotNull Option options
) {
    public record Option(
            boolean personalInformation,
            boolean sendingPromoteMessage
    ){}
}
