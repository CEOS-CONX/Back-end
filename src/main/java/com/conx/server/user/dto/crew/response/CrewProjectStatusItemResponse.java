package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewWorkspaceProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CrewProjectStatusItemResponse(
        long applicationId,
        long projectId,

        CrewWorkspaceProjectStatus status,

        String projectImage,
        String projectName,
        String brandName,
        String companyName,

        Industry category,
        ProjectType projectType,

        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline,

        Long subsidy,
        LocalDateTime registeredAt
) {

    public static CrewProjectStatusItemResponse from(
            ProjectApplication application
    ) {
        Project project =
                application.getProject();

        return new CrewProjectStatusItemResponse(
                application.getId(),
                project.getId(),

                CrewWorkspaceProjectStatus.from(
                        application.getStatus(),
                        project.getStatus()
                ),

                project.getProjectImage(),
                project.getName(),
                project.getBrandName(),
                project.getCompany().getCompanyName(),

                project.getCompany().getIndustry(),
                project.getProjectType(),

                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubmitDeadline(),

                project.getSubsidy(),
                application.getCreatedAt()
        );
    }
}