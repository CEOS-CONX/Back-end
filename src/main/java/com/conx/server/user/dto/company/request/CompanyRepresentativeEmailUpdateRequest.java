package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyRepresentativeEmailUpdateRequest(
        @NotBlank
        @Email
        String representativeEmail
) {
}