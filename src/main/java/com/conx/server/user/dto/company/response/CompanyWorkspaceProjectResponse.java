package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record CompanyWorkspaceProjectResponse(
        Long projectId,
        Long deadlineCount,
        ProjectStatus status,

        List<String> projectImage,
        String name,
        String companyName,
        Industry industry,
        ProjectType projectType,

        LocalDate projectStartDate,
        LocalDate projectDeadline,
        int views
) {

    public static CompanyWorkspaceProjectResponse from(Project project) {
        long deadlineCount = ChronoUnit.DAYS.between(
                project.getRecruitDeadLine(), LocalDate.now()
        );

        return new CompanyWorkspaceProjectResponse(
                project.getId(),
                deadlineCount,
                project.getStatus(),

                project.getProjectImage(),
                project.getProjectName(),
                project.getCompanyName(),
                project.getIndustry(),
                project.getProjectType(),

                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getViews()
        );
    }
}