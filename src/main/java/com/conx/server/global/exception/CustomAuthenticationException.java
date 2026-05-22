package com.conx.server.global.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {
    private final ErrorCode code;

    public CustomAuthenticationException(ErrorCode code) {
        super(code.getErrorMessage());
        this.code = code;
    }

    public CustomAuthenticationException(CustomException ce){
        super(ce.getLocalizedMessage());
        this.code = ce.getCode();
    }
}
