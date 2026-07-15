package com.conx.server.landingPage.dto;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.util.List;

public record ProjectWrapperForLandingPageDTO(
    long projectId,
    List<String> projectImageLink,

    String projectName,
    String companyName,
    Industry industry,
    ProjectType projectType,
    LocalDate projectStartDate,
    LocalDate projectDeadline
) {
    public static ProjectWrapperForLandingPageDTO from(Project p){
        return new ProjectWrapperForLandingPageDTO(
                p.getId(), p.getProjectImage(),
                p.getProjectName(), p.getCompanyName(), p.getIndustry(), p.getProjectType(), p.getProjectStartDate(), p.getProjectDeadline()
        );
    }
}
