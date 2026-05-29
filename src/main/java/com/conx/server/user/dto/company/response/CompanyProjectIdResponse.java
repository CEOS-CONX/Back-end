package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

public record CompanyProjectIdResponse(
        Long projectId,
        ProjectStatus status
) {

    public static CompanyProjectIdResponse from(Project project) {
        return new CompanyProjectIdResponse(
                project.getId(),
                project.getStatus()
        );
    }
}