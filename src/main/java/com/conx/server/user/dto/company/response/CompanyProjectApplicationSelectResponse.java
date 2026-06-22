package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectStatus;

public record CompanyProjectApplicationSelectResponse(
        Long projectId,
        Long applicationId,
        Long selectedCrewId,
        ProjectStatus projectStatus,
        ProjectApplicationStatus applicationStatus
) {

    public static CompanyProjectApplicationSelectResponse of(
            Project project,
            ProjectApplication application
    ) {
        return new CompanyProjectApplicationSelectResponse(
                project.getId(),
                application.getId(),
                application.getCrew().getId(),
                project.getStatus(),
                application.getStatus()
        );
    }
}