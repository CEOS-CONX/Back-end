package com.conx.server.project.domain.enums;

//개발하면서 수정 및 확장될 수 있습니다..
//자유롭게 수정해주세요
public enum ProjectStatus {

    DRAFT,
    //임시저장

    RECRUITING,
    //크루 모집 중

    CONTRACT_PENDING,
    //계약서 작성 대기 중

    PROGRESS,
    //프로젝트 진행 중

    WAITING_RESULT,
    //프로젝트 진행 후 크루의 결과물 제출 대기 중

    INSPECTION,
    //크루가 결과물 제출을 완료함. 기업이 검수 중

    ADJUSTING,
    //정산 중

    ADJUSTED,
    //지금 완료

    DONE,
    //완전 완료

    EXPIRED
    //크루가 선정되지 않고 만료됨
}