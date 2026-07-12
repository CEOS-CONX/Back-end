package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;

import java.time.LocalDate;

public record CrewProjectHistoryResponse(
        Long projectId,
        ProjectStatus status,
        String projectName,
        String brandName,
        ProjectType projectType,
        String platformName,
        String contentType,
        Double point,
        LocalDate projectStartDate,
        LocalDate projectDeadline
) {

    public static CrewProjectHistoryResponse from(
            Project project,
            Double point
    ) {
        return new CrewProjectHistoryResponse(
                project.getId(),
                project.getStatus(),
                project.getName(),
                project.getBrandName(),
                project.getProjectType(),
                project.getPlatformName(),
                project.getContentType(),
                point,
                project.getProjectStartDate(),
                project.getProjectDeadline()
        );
    }
}