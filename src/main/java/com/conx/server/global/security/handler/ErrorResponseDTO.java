package com.conx.server.global.security.handler;

import com.conx.server.global.exception.ErrorCode;

public record ErrorResponseDTO(
        String errCode, String errMessage
){
    public static ErrorResponseDTO create(ErrorCode e){
        return new ErrorResponseDTO(
                e.getErrorCode(), e.getErrorMessage()
        );
    }
}