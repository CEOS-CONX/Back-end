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
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST,
            "V001",
            "이메일 인증요청을 보내지 않았거나, 인증번호가 만료되었습니다."
    ),
    CODE_UNMATCHED(
            HttpStatus.BAD_REQUEST,
            "V002",
            "인증번호가 일치하지 않습니다"
    ),
    USER_UNVERIFIED(
            HttpStatus.UNAUTHORIZED,
            "V003",
            "이메일 인증이 진행되지 않았습니다"
    ),
    EMAIL_ALREADY_IN_USE(
            HttpStatus.CONFLICT,
            "V004",
            "이미 사용 중인 이메일입니다."
    ),
    EMAIL_SAME_AS_CURRENT(
            HttpStatus.BAD_REQUEST,
            "V005",
            "현재 사용 중인 이메일과 동일합니다."
    ),
    EMAIL_CHANGE_VERIFICATION_INVALID(
            HttpStatus.BAD_REQUEST,
            "V006",
            "이메일 변경 인증이 만료되었거나 유효하지 않습니다."
    ),
    PASSWORD_RESET_VERIFICATION_INVALID(
            HttpStatus.BAD_REQUEST,
            "V007",
            "비밀번호 재설정 인증이 만료되었거나 유효하지 않습니다."
    ),

    //Error for Signup
    USER_ALREADY_EXITS(HttpStatus.BAD_REQUEST, "S001", "이미 가입한 사용자입니다"),
    UNFILLED_BLANK(HttpStatus.BAD_REQUEST, "S002", "빈칸을 모두 채워주세요"),
    PASSWORD_DOUBLE_CHECK_FAILED(HttpStatus.BAD_REQUEST, "S003", "비밀번호를 확인해주세요"),
    INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "S004", "잘못된 사용자타입입니다"),

    //Error for Category
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "C001", "잘못된 카테고리값입니다."),

    //Error for Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "알림이 없습니다."),
    MISMATCH_NOTIFICATION_RECEIVER(HttpStatus.BAD_REQUEST, "N002", "삭제 요청 클라이언트가 알림 수신자가 아닙니다."),

    //Error for Project
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "프로젝트 정보를 찾을 수 없습니다."),
    INVALID_PROJECT_STATUS(HttpStatus.BAD_REQUEST, "P002", "현재 프로젝트 상태에서는 처리할 수 없습니다."),
    PROJECT_ALREADY_END(HttpStatus.BAD_REQUEST, "P003", "이미 끝난 프로젝트입니다."),
    PROJECT_CONTRACT_UNSIGNED(HttpStatus.BAD_REQUEST, "P004", "계약서가 작성되지 않았습니다."),

    //Error for Project Question
    PROJECT_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PQ001", "프로젝트 질문을 찾을 수 없습니다."),
    PROJECT_QUESTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PQ002", "프로젝트 질문에 접근할 권한이 없습니다."),
    PROJECT_QUESTION_ANSWER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PQ003", "프로젝트 질문에 답변할 권한이 없습니다."),

    //Error for Project Application
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "AP001", "지원서를 찾을 수 없습니다."),
    INVALID_APPLICATION_STATUS(HttpStatus.BAD_REQUEST, "AP002", "현재 지원서 상태에서는 처리할 수 없습니다."),
    APPLICATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AP003", "이미 지원한 프로젝트입니다."),

    //Error for Project Bookmark
    PROJECT_BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PB001", "이미 북마크한 프로젝트입니다."),
    PROJECT_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "PB002", "프로젝트 북마크를 찾을 수 없습니다."),

    //Error for Partner Crew
    PARTNER_CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "CR002", "선정된 파트너 크루를 찾을 수 없습니다."),

    //Error for Project Submission
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SM001", "제출된 결과물을 찾을 수 없습니다."),
    INVALID_SUBMISSION_STATUS(HttpStatus.BAD_REQUEST, "SM002", "현재 결과물 상태에서는 처리할 수 없습니다."),

    //Error for Project Evaluation
    PROJECT_EVALUATION_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "EV001",
            "이미 평가한 프로젝트입니다."
    ),
    PROJECT_EVALUATION_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "EV002",
            "현재 프로젝트 상태에서는 크루를 평가할 수 없습니다."
    ),

    //Error for Settlement
    SETTLEMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ST001",
            "정산 정보를 찾을 수 없습니다."
    ),
    SETTLEMENT_ALREADY_PAID(
            HttpStatus.CONFLICT,
            "ST002",
            "이미 지급 완료된 정산입니다."
    ),

    //Error for Representative Project
    REPRESENTATIVE_PROJECT_LIMIT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "RP001",
            "대표 프로젝트는 최대 3개까지 선택할 수 있습니다."
    ),
    REPRESENTATIVE_PROJECT_DUPLICATED(
            HttpStatus.BAD_REQUEST,
            "RP002",
            "동일한 프로젝트를 중복하여 선택할 수 없습니다."
    ),
    REPRESENTATIVE_PROJECT_NOT_AVAILABLE(
            HttpStatus.BAD_REQUEST,
            "RP003",
            "대표 프로젝트로 선택할 수 없는 프로젝트가 포함되어 있습니다."
    ),

    //Error for Criteria
    CRITERIA_NOT_FOUND(HttpStatus.NOT_FOUND, "PC001", "기준을 찾을 수 없습니다."),

    //Error for Portfolio
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "CR002", "포트폴리오를 찾을 수 없습니다.");


    private final HttpStatus status;

    private final String errorCode;

    private final String errorMessage;

    ErrorCode(HttpStatus status, String code, String message){
        this.status = status;
        this.errorCode = code;
        this.errorMessage = message;
    }
}
