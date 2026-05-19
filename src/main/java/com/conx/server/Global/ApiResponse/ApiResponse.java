package com.conx.server.Global.ApiResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String message;
    private T data;
    private Meta meta;

    public static <T> ApiResponse<T> of(
            String message,
            T data,
            Page<?> page
    ) {
        return new ApiResponse<>(
                message,
                data,
                Meta.from(page)
        );
    }

    public static <T> ApiResponse<T> ofEmpty(){
        return new ApiResponse<>(
                "성공", null, Meta.from(Page.empty())
        );
    }
}
