package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;

import java.time.LocalDate;

public record CompanyWorkspaceProjectResponse(
        Long projectId,
        String projectImage,
        String name,
        String brandName,
        ProjectType projectType,
        ProjectStatus status,
        LocalDate recruitDeadLine,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline,
        long subsidy,
        boolean incentive,
        int views
) {

    public static CompanyWorkspaceProjectResponse from(Project project) {
        return new CompanyWorkspaceProjectResponse(
                project.getId(),
                project.getProjectImage(),
                project.getName(),
                project.getBrandName(),
                project.getProjectType(),
                project.getStatus(),
                project.getRecruitDeadLine(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubmitDeadline(),
                project.getSubsidy(),
                project.isIncentive(),
                project.getViews()
        );
    }
}