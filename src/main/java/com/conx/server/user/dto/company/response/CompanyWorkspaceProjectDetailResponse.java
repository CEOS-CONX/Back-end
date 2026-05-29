package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;

import java.time.LocalDate;
import java.util.List;

public record CompanyWorkspaceProjectDetailResponse(
        Long projectId,
        String projectImage,
        String brandName,
        String name,
        String objectives,
        ProjectType projectType,
        String requirement,
        String projectExplanation,
        String resultForm,
        String essentialSubmitPart,
        LocalDate recruitDeadLine,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline,
        CrewType crewType,
        String competency,
        String preferenceCondition,
        long subsidy,
        boolean incentive,
        String incentiveCondition,
        List<String> additionalFileLinks,
        String referenceLink,
        ProjectStatus status,
        int views,
        String managerName,
        String managerEmail,
        String managerPhone,
        Long selectedCrewId
) {

    public static CompanyWorkspaceProjectDetailResponse from(Project project) {
        Long selectedCrewId = null;

        if (project.getSelectedCrew() != null) {
            selectedCrewId = project.getSelectedCrew().getId();
        }

        return new CompanyWorkspaceProjectDetailResponse(
                project.getId(),
                project.getProjectImage(),
                project.getBrandName(),
                project.getName(),
                project.getObjectives(),
                project.getProjectType(),
                project.getRequirement(),
                project.getProjectExplanation(),
                project.getResultForm(),
                project.getEssentialSubmitPart(),
                project.getRecruitDeadLine(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubmitDeadline(),
                project.getCrewType(),
                project.getCompetency(),
                project.getPreferenceCondition(),
                project.getSubsidy(),
                project.isIncentive(),
                project.getIncentiveCondition(),
                project.getAdditionalFileLinks(),
                project.getReferenceLink(),
                project.getStatus(),
                project.getViews(),
                project.getManagerName(),
                project.getManagerEmail(),
                project.getManagerPhone(),
                selectedCrewId
        );
    }
}