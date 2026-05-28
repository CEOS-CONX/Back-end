package com.conx.server.notification.domain;

public enum NotificationType {
    MAIL("%s"), //CONX를 통해 작성한 메일 도착 알림. 메일 제목이 들어갈 예정
    CLOSE_TO_END("%s 프로젝트가 %d일 뒤 마감됩니다."), //프로젝트 마감기한 임박. 프로젝트 이름과 마감까지 남은 날짜

    //기업용 알림
    RESULT_UPLOADED("%s 프로젝트에 최종결과물이 등록되었습니다."), //크루의 최종결과물 등록, 프로젝트 이름

    //크루용 알림
    PROJECT_SELECTED("%s 프로젝트에 선정되었습니다."), //프로젝트에 선정됨. 프로젝트 이름
    CLOSE_TO_END_OF_MARKED_PROJECT("북마크한 %s 프로젝트가 %d일 뒤 마감됩니다."), //북마크한 프로젝트 마감기한 임박. 프로젝트 이름과 마감까지 남은 날짜
    ADJUSTMENT_DONE("%s 프로젝트에 정산이 완료되었습니다."); //정산 완료. 프로젝트 이름

    private final String template;

    NotificationType(String template){
        this.template = template;
    }

    public String format(Object... args){
        return String.format(template, args);
    }
}
