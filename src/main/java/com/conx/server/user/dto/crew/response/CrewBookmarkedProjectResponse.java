package com.conx.server.user.dto.crew.response;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;

public record CrewBookmarkedProjectResponse(
        Long bookmarkId,
        Long projectId,
        String projectImage,
        String projectName,
        String companyName,
        Industry industry,
        ProjectType projectType,
        ProjectStatus projectStatus,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        long subsidy,
        boolean incentive
) {

    public static CrewBookmarkedProjectResponse from(ProjectBookmark bookmark) {
        Project project = bookmark.getProject();

        return new CrewBookmarkedProjectResponse(
                bookmark.getId(),
                project.getId(),
                project.getProjectImage(),
                project.getName(),
                project.getCompany().getCompanyName(),
                project.getCompany().getIndustry(),
                project.getProjectType(),
                project.getStatus(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubsidy(),
                project.isIncentive()
        );
    }
}