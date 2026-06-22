package com.conx.server.user.dto.signupRequest;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequestDTO(
        @NotBlank(message = "이메일을 입력해주세요") @Email String email,
        @NotBlank(message = "비밀번호를 입력해주세요") String password,
        @NotNull(message = "재확인 비밀번호를 입력해주세요") String passwordCheck,
        @NotNull(message = "개인정보 사용동의를 확인해주세요") Option options
) {
    public record Option(
            boolean personalInformation,
            boolean sendingPromoteMessage
    ){}

    public void passwordDoubleChecking(){
        if(!password.equals(passwordCheck)){
            throw new CustomException(ErrorCode.PASSWORD_DOUBLE_CHECK_FAILED);
        };
    }
}
