package com.conx.server.User.DTO.SignupRequest;

import com.conx.server.User.Domain.Enum.Industry;
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
