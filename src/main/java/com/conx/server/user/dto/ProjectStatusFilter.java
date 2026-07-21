package com.conx.server.user.dto;

import com.conx.server.project.domain.enums.ProjectStatus;
import lombok.Getter;

import java.util.List;

// 기업 프로젝트 목록 조회 시 사용하는 상태 필터 그룹
// 실제 ProjectStatus를 그룹핑해서 화면상의 필터(전체/모집중/진행중/검수대기/정산대기/정산완료)와 매핑한다.
@Getter
public enum ProjectStatusFilter {

    // 모집중,
    RECRUITING(List.of(ProjectStatus.RECRUITING)),

    // 진행중 (계약서 작성대기, 진행 중)
    IN_PROGRESS(List.of(ProjectStatus.CONTRACT_PENDING, ProjectStatus.PROGRESS)),

    // 검수대기 (결과물 대기, 기업 검수 중)
    INSPECTION_WAITING(List.of(ProjectStatus.WAITING_RESULT, ProjectStatus.INSPECTION)),

    // 정산대기
    SETTLEMENT_WAITING(List.of(ProjectStatus.ADJUSTING)),

    // 정산완료
    SETTLEMENT_DONE(List.of(ProjectStatus.ADJUSTED, ProjectStatus.DONE));

    private final List<ProjectStatus> statuses;

    ProjectStatusFilter(List<ProjectStatus> statuses) {
        this.statuses = statuses;
    }
}