package com.conx.server.project.dto.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record ProjectBrowseResponse(
        boolean isImminent,
        boolean isBookmarked,

        Long projectId,
        List<String> projectImage,
        String projectName,
        String companyName,
        Industry category,
        ProjectType projectType,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        long subsidy,
        boolean incentive
) {

    public static ProjectBrowseResponse from(Project project, boolean isBookmarked) {
        int dayBeforeDeadline = (int) ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getRecruitDeadLine()
        );

        boolean isImminent = dayBeforeDeadline >= 0 && dayBeforeDeadline <= 3;

        return new ProjectBrowseResponse(
                isImminent,
                isBookmarked,
                project.getId(),
                project.getProjectImage(),
                project.getProjectName(),
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