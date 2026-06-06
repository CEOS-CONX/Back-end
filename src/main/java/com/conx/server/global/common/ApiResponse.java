package com.conx.server.global.common;

import com.conx.server.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String status,
        String message,
        T payload,
        Boolean hasNotification
) {

    static <T> ApiResponse<T> success(String message, T payload, Boolean hasNotification) {
        return new ApiResponse<>("success", message, payload, hasNotification);
    }

    static <T> ApiResponse<T> success(T payload, Boolean hasNotification) {
        return new ApiResponse<>("success", "요청이 성공했습니다.", payload, hasNotification);
    }

    public static ApiResponse<?> success(Boolean hasNotification){
        return new ApiResponse<>("success", "요청이 성공했습니다.", null, hasNotification);
    }

    public static ApiResponse<?> success(String message, Boolean hasNotification) {
        return new ApiResponse<>("success", message, null, hasNotification);
    }

    public static ApiResponse<?> fail(String status, String message) {
        return new ApiResponse<>(status, message, null, null);
    }

    public static ApiResponse<?> fail(ErrorCode errorCode){
        return new ApiResponse<>(errorCode.getErrorCode(), errorCode.getErrorMessage(), null, null);
    }
}