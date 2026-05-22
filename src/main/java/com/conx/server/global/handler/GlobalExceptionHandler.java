package com.conx.server.global.handler;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException exception) {
        ErrorCode errorCode = exception.getCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getErrorMessage()));
    }

    /*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.INVALID_.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus())
                .body(ApiResponse.fail(message));
    }
    졸음이슈로 나중에 수정하기

     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Unexpected exception occurred", exception);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage()));
    }
}