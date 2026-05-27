package com.conx.server.landingPage.dto;

import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;

public record ProjectWrapperForDashBoardDTO(
    long projectId,
    String projectImageLink,

    String projectName,
    String companyName,
    Industry industry,
    ProjectType projectType,
    LocalDate projectStartDate,
    LocalDate projectDeadline
) {
}
