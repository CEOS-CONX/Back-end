package com.conx.server.project.dto.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;

public record ProjectBrowseResponse(
        Long projectId,
        String projectImage,
        String projectName,
        String companyName,
        Industry category,
        ProjectType projectType,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        long subsidy,
        boolean incentive
) {

    public static ProjectBrowseResponse from(Project project) {
        return new ProjectBrowseResponse(
                project.getId(),
                project.getProjectImage(),
                project.getName(),
                project.getCompany().getCompanyName(),
                project.getCompany().getIndustry(),
                project.getProjectType(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubsidy(),
                project.isIncentive()
        );
    }
}