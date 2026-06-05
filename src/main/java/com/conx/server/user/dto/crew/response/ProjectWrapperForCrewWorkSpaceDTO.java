package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ProjectWrapperForCrewWorkSpaceDTO(
        long projectId,

        ProjectStatus projectStatus,
        String projectName,
        String brandName,
        LocalDate submitDeadline,

        long lastDays
) {
    public static ProjectWrapperForCrewWorkSpaceDTO create(Project project){
        long lastDays = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getSubmitDeadline()
        );

        return new ProjectWrapperForCrewWorkSpaceDTO(project.getId(), project.getStatus(),
                project.getName(), project.getBrandName(), project.getSubmitDeadline(), lastDays);
    }
}
