package com.conx.server.user.dto.signupRequest;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.types.CrewType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCrewUserDTO(
        @NotBlank(message = "이메일을 입력해주세요") @Email String email,
        @NotBlank(message = "크루명을 입력해주세요") String crewName,
        @NotNull(message = "크루 유형을 입력해주세요") CrewType crewType,
        @NotBlank(message = "담당자 이름을 입력해주세요") String managerName,
        @NotBlank(message = "직무 이름을 입력해주세요") String job,
        String customCrewType
) {
    public void validateCrewType() {
        if (crewType.equals(CrewType.ETC) && customCrewType==null){
            throw new CustomException(ErrorCode.UNFILLED_BLANK);
        }
    }
}
