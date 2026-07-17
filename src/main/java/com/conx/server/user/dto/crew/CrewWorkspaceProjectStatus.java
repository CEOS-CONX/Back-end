package com.conx.server.user.dto.crew;

import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectStatus;

public enum CrewWorkspaceProjectStatus {

    APPLIED,
    // 지원 완료, 선정 대기

    IN_PROGRESS,
    // 계약 대기 또는 프로젝트 진행 중

    EXECUTION_COMPLETED,
    // 프로젝트 수행 완료, 결과물 제출 대기

    SUBMISSION_COMPLETED,
    // 결과물 제출 완료 또는 정산 중

    SETTLEMENT_COMPLETED;
    // 정산 완료

    public static CrewWorkspaceProjectStatus from(
            ProjectApplicationStatus applicationStatus,
            ProjectStatus projectStatus
    ) {
        if (applicationStatus == ProjectApplicationStatus.PENDING) {
            return APPLIED;
        }

        if (applicationStatus != ProjectApplicationStatus.SELECTED) {
            throw new IllegalArgumentException(
                    "프로젝트 현황으로 변환할 수 없는 지원 상태입니다."
            );
        }

        return switch (projectStatus) {
            case CONTRACT_PENDING, PROGRESS ->
                    IN_PROGRESS;

            case WAITING_RESULT ->
                    EXECUTION_COMPLETED;

            case INSPECTION, ADJUSTING ->
                    SUBMISSION_COMPLETED;

            case DONE ->
                    SETTLEMENT_COMPLETED;

            default ->
                    throw new IllegalArgumentException(
                            "프로젝트 현황으로 변환할 수 없는 프로젝트 상태입니다."
                    );
        };
    }
}