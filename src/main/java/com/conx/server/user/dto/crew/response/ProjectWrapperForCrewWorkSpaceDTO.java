package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record ProjectWrapperForCrewWorkSpaceDTO(
        long projectId,

        ProjectStatus projectStatus,
        String projectName,
        String brandName,
        LocalDate submitDeadline

) {
}
