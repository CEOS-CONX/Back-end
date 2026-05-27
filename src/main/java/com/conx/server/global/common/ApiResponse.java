package com.conx.server.global.common;

import com.conx.server.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String status,
        String message,
        T payload
) {

    public static <T> ApiResponse<T> success(String message, T payload) {
        return new ApiResponse<>("success", message, payload);
    }

    public static <T> ApiResponse<T> success(T payload) {
        return new ApiResponse<>("success", "요청이 성공했습니다.", payload);
    }

    public static ApiResponse<?> success(){
        return new ApiResponse<>("success", "요청이 성공했습니다.", null);
    }

    public static ApiResponse<?> success(String message) {
        return new ApiResponse<>("success", message, null);
    }


    public static ApiResponse<?> fail(String status, String message) {
        return new ApiResponse<>(status, message, null);
    }

    public static ApiResponse<?> fail(ErrorCode errorCode){
        return new ApiResponse<>(errorCode.getErrorCode(), errorCode.getErrorMessage(), null);
    }
}