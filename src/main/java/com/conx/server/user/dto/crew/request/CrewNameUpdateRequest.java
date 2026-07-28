package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;

public record CrewNameUpdateRequest(
        @NotBlank
        String name
) {
}
