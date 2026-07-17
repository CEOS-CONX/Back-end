package com.conx.server.project.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewProjectTodoType {

    RESULT_SUBMISSION(
            "프로젝트 결과물 제출"
    ),

    REVISION_SUBMISSION(
            "수정 결과물 제출"
    ),

    SETTLEMENT_CONFIRMATION(
            "정산 정보 확인"
    );

    private final String taskName;
}