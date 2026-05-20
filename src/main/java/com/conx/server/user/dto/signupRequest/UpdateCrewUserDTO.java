package com.conx.server.user.dto.signupRequest;

import com.conx.server.user.domain.types.CrewType;
import jakarta.validation.constraints.NotNull;

public record UpdateCrewUserDTO(
        @NotNull String email,
        @NotNull String crewName,
        @NotNull CrewType crewType,
        @NotNull String managerName,
        @NotNull String job,
        @NotNull String customCrewType
) {
}
