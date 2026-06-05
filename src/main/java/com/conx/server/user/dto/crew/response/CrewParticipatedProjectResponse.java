package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record CrewParticipatedProjectResponse(
        Long projectId,
        String projectName,
        String brandName,
        ProjectStatus status,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline
) {
    public static CrewParticipatedProjectResponse from(Project project) {
        return new CrewParticipatedProjectResponse(
                project.getId(),
                project.getName(),
                project.getBrandName(),
                project.getStatus(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubmitDeadline()
        );
    }
}