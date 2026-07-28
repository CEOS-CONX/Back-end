package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CrewRepresentativeEmailUpdateRequest(
        @NotBlank
        @Email
        String representativeEmail
) {
}
