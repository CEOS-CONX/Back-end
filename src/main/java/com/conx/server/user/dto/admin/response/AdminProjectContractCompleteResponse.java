package com.conx.server.user.dto.admin.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

public record AdminProjectContractCompleteResponse(
        Long projectId,
        ProjectStatus status,
        String message
) {
    public static AdminProjectContractCompleteResponse from(Project project) {
        return new AdminProjectContractCompleteResponse(
                project.getId(),
                project.getStatus(),
                "계약서 작성완료 처리되었습니다."
        );
    }
}