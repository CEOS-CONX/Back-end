package com.conx.server.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode code;

    public CustomException(ErrorCode code){
        super(code.getErrorMessage());
        this.code = code;
    }
}