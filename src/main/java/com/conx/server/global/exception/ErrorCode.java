package com.conx.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
     /*
    400: badRequest
    401: Unauthorized
    403: forbidden
    404: NotFound
    500: InternalServerError
     */

    //Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버에 장애가 발생했습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "G002", "해당 이메일을 가진 사용자를 찾을 수 없습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "G003", "해당 자원을 찾을 수 없습니다"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G004", "유효하지 않은 메서드인자입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "G005", "해당 페이지에 대한 권한이 없습니다."),
    METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED, "G006", "지원하지 않는 HTTP 메서드입니다"),

    //Error in SendingEmails
    INVALID_EMAIL_TYPE(HttpStatus.BAD_REQUEST, "M001", "잘못된 형식의 이메일 주소입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "M002", "이메일 전송을 위한 계정 로그인에 실패했습니다."),
    SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M003", "메시지 전송에 실패했습니다."),

    //Error in Login
    PASSWORD_UNMATCHED(HttpStatus.BAD_REQUEST, "A001", "비밀번호가 일치하지 않습니다"),
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "A002", "토큰 재사용이 감지되었습니다"),

    //Error in Token Parsing
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "T001", "토큰이 만료되었습니다."),
    INVALID_TOKEN_FORM(HttpStatus.BAD_REQUEST, "T002", "잘못된 형식의 토큰입니다."),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "T003", "서명이 잘못되었습니다."),
    INTERNAL_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "T004", "토큰에 오류가 있습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "T005", "리프레시 토큰을 찾을 수 없습니다"),

    //Error for email verification
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "V001", "이메일 인증요청을 보내지 않았거나, 인증번호가 만료되었습니다."),
    CODE_UNMATCHED(HttpStatus.BAD_REQUEST, "V002", "인증번호가 일치하지 않습니다"),
    USER_UNVERIFIED(HttpStatus.UNAUTHORIZED, "V003", "이메일 인증이 진행되지 않았습니다"),

    //Error for Signup
    USER_ALREADY_EXITS(HttpStatus.BAD_REQUEST, "S001", "이미 가입한 사용자입니다"),
    UNFILLED_BLANK(HttpStatus.BAD_REQUEST, "S002", "빈칸을 모두 채워주세요"),
    PASSWORD_DOUBLE_CHECK_FAILED(HttpStatus.BAD_REQUEST, "S003", "비밀번호를 확인해주세요"),
    INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "S004", "잘못된 사용자타입입니다"),

    //Error for Category
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "C001", "잘못된 카테고리값입니다."),

    //Error for Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "알림이 없습니다."),
    MISMATCH_NOTIFICATION_RECEIVER(HttpStatus.BAD_REQUEST, "N002", "삭제 요청 클라이언트가 알림 수신자가 아닙니다.");



    private final HttpStatus status;

    private final String errorCode;

    private final String errorMessage;

    ErrorCode(HttpStatus status, String code, String message){
        this.status = status;
        this.errorCode = code;
        this.errorMessage = message;
    }
}