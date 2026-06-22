package com.conx.server.user.dto.signupRequest;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.types.Industry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCompanyUserDTO(
        @NotBlank(message = "이메일을 입력해주세요") @Email String email,

        @NotBlank(message = "브랜드이름을 입력해주세요") String brandName,
        @NotNull(message = "카테고리를 골라주세요") Industry industry,
        @NotBlank(message = "관리자명을 입력해주세요") String managerName,
        @NotBlank(message = "직무를 입력해주세요") String job,
        String customIndustry
) {
    public void validateIndustry(){
        if (industry.equals(Industry.ETC) && customIndustry==null){
            throw new CustomException(ErrorCode.UNFILLED_BLANK);
        }
    }
}
