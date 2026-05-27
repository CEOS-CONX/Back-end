package com.conx.server.user.dto.signupRequest;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignupRequestDTO(
        @NotNull @Email String email,
        @NotNull String password,
        @NotNull String passwordCheck,
        @NotNull Option options
) {
    public record Option(
            boolean personalInformation,
            boolean sendingPromoteMessage
    ){}

    public void passwordDoubleChecking(){
        if(password.equals(passwordCheck)){
            throw new CustomException(ErrorCode.PASSWORD_DOUBLE_CHECK_FAILED);
        };
    }
}
