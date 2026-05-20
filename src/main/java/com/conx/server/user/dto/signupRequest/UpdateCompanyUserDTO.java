package com.conx.server.user.dto.signupRequest;

import com.conx.server.user.domain.types.Industry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UpdateCompanyUserDTO(
        @NotNull @Email String email,

        String brandName,
        Industry industry,
        String managerName,
        String job,
        String customIndustry
) {
}
