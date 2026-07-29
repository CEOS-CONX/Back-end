package com.conx.server.notification.domain;

public enum NotificationType {
    MAIL("%s"), //CONX를 통해 작성한 메일 도착 알림. 메일 제목이 들어갈 예정
    CLOSE_TO_END_OF_RECRUITING("%s의 모집 마감이 %d일 남았습니다."), //프로젝트 마감기한 임박. 프로젝트 이름과 마감까지 남은 날짜

    //기업용 알림
    RESULT_UPLOADED("%s의 결과물이 등록되었습니다."), //크루의 최종결과물 등록, 프로젝트 이름
    QUESTION_REGISTERED("%s에 새로운 문의가 등록되었습니다."), //담당자 Q&A 등록, 프로젝트 이름

    //크루용 알림
    PROJECT_SELECTED("%s에 선정되었습니다."), //프로젝트에 선정됨. 프로젝트 이름
    PROJECT_REJECTED("%s에 선정되지 않았습니다."), //프로젝트에 선정되지 않음. 프로젝트 이름
    QUESTION_ANSWER_REGISTERED("%s 문의에 답변이 등록되었습니다."), //프로젝트에 담당자 Q&A에 답변이 등록됨. 프로젝트 이름

    CLOSE_TO_END_OF_MARKED_PROJECT("북마크한 %s의 모집 마감이 %d일 남았습니다."), //북마크한 프로젝트 마감기한 임박. 프로젝트 이름과 마감까지 남은 날짜

    ADJUSTMENT_DONE("%s의 정산이 완료되었습니다."), //정산 완료. 프로젝트 이름

    PROJECT_CLOSE_TO_END("%s의 프로젝트 마감이 %d일 남았습니다."),

    RESULT_UPLOAD_CLOSE_TO_END("%s의 결과물 제출 마감이 %d일 남았습니다."), //프로젝트 제출기한 마감 전, 프로젝트 이름과 마감까지 남은 날짜

    LATE_FOR_SUBMIT_DEADLINE("%s의 결과물 제출 기한이 %d일 지났습니다."); //프로젝트 마감기한 오버, 상동

    private final String template;

    NotificationType(String template){
        this.template = template;
    }

    public String format(Object... args){
        return String.format(template, args);
    }
}
