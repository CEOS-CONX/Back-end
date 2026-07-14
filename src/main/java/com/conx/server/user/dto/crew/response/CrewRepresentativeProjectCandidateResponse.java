package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;

import java.time.LocalDate;

public record CrewRepresentativeProjectCandidateResponse(
        Long projectId,
        String projectName,
        String brandName,
        ProjectStatus status,
        ProjectType projectType,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        boolean selected
) {

    public static CrewRepresentativeProjectCandidateResponse from(
            Project project,
            boolean selected
    ) {
        return new CrewRepresentativeProjectCandidateResponse(
                project.getId(),
                project.getName(),
                project.getBrandName(),
                project.getStatus(),
                project.getProjectType(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                selected
        );
    }
}