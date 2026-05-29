package com.conx.server.project.dto.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.util.List;

public record ProjectBrowseDetailResponse(
        Long projectId,
        String projectImage,
        String projectName,
        String projectExplanation,
        String objectives,
        String brandName,

        Long companyId,
        String companyName,
        String companyProfileImage,
        Industry companyIndustry,

        ProjectStatus status,
        ProjectType projectType,

        LocalDate recruitDeadLine,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline,

        CrewType crewType,
        String competency,
        String preferenceCondition,

        String requirement,
        String resultForm,
        String essentialSubmitPart,

        long subsidy,
        boolean incentive,
        String incentiveCondition,

        List<String> additionalFileLinks,
        String referenceLink,
        int views
) {

    public static ProjectBrowseDetailResponse from(Project project) {
        return new ProjectBrowseDetailResponse(
                project.getId(),
                project.getProjectImage(),
                project.getName(),
                project.getProjectExplanation(),
                project.getObjectives(),
                project.getBrandName(),

                project.getCompany().getId(),
                project.getCompany().getCompanyName(),
                project.getCompany().getProfileImage(),
                project.getCompany().getIndustry(),

                project.getStatus(),
                project.getProjectType(),

                project.getRecruitDeadLine(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubmitDeadline(),

                project.getCrewType(),
                project.getCompetency(),
                project.getPreferenceCondition(),

                project.getRequirement(),
                project.getResultForm(),
                project.getEssentialSubmitPart(),

                project.getSubsidy(),
                project.isIncentive(),
                project.getIncentiveCondition(),

                project.getAdditionalFileLinks(),
                project.getReferenceLink(),
                project.getViews()
        );
    }
}