package com.conx.server.user.dto.signupRequest;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
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
    public void validateCrewType() {
        if (crewType.equals(CrewType.ETC) && customCrewType==null){
            throw new CustomException(ErrorCode.UNFILLED_BLANK);
        }
    }
}
