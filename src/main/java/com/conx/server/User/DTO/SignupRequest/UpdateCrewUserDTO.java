package com.conx.server.User.DTO.SignupRequest;

import com.conx.server.User.Domain.Enum.CrewType;
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
