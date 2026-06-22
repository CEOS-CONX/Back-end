package com.conx.server.project.dto.response;

import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;

public record ProjectApplicationResponse(
        Long applicationId,
        Long projectId,
        Long crewId,
        ProjectApplicationStatus status
) {

    public static ProjectApplicationResponse from(ProjectApplication application) {
        return new ProjectApplicationResponse(
                application.getId(),
                application.getProject().getId(),
                application.getCrew().getId(),
                application.getStatus()
        );
    }
}