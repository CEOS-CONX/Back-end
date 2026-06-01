package com.conx.server.project.dto.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record ProjectWrapperForCrewWorkSpaceDTO(
        long projectId,
        String brandName,
        LocalDate submitDeadline,
        ProjectStatus projectStatus
) {
    public static ProjectWrapperForCrewWorkSpaceDTO create (Project project){
        return new ProjectWrapperForCrewWorkSpaceDTO(
                project.getId(), project.getBrandName(), project.getSubmitDeadline(), project.getStatus()
        );
    }
}
