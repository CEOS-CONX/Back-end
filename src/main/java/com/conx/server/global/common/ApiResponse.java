package com.conx.server.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>("success", message, null);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>("fail", message, null);
    }
}