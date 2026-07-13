package com.conx.server.user.dto.company.response;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.project.domain.AdditionalLinksWrapper;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.response.ResultFormResponse;
import com.conx.server.user.domain.types.CrewType;

import java.time.LocalDate;
import java.util.List;

public record CompanyWorkspaceProjectDetailResponse(
        Long projectId,

        List<String> projectImage,

        String brandName,
        String projectName,
        String projectExplanation,

        ProjectType projectType,
        List<ResultFormResponse> resultForm,

        LocalDate recruitDeadLine,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline,

        CrewType crewType,
        int peopleNumber,
        String competency,
        String preferenceCondition,

        long subsidy,
        boolean incentive,
        String incentiveCondition,

        List<FileResponseDTO> files,

        List<AdditionalLinksWrapper> links,

        ProjectStatus status,
        int views,

        String managerName,
        String managerEmail,

        Long selectedCrewId
) {

    public static CompanyWorkspaceProjectDetailResponse from(Project project,
                                                             List<FileResponseDTO> files) {
        return new CompanyWorkspaceProjectDetailResponse(
                project.getId(),

                project.getProjectImage(),

                project.getBrandName(),
                project.getProjectName(),
                project.getProjectExplanation(),

                project.getProjectType(),
                project.getResultForm().stream().map(ResultFormResponse::from).toList(),

                project.getRecruitDeadLine(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getSubmitDeadline(),

                project.getCrewType(),
                project.getPeopleNumber(),
                project.getCompetency(),
                project.getPreferenceCondition(),

                project.getSubsidy(),
                project.isIncentive(),
                project.getIncentiveCondition(),

                files,
                project.getLinks(),

                project.getStatus(),
                project.getViews(),

                project.getManagerName(),
                project.getManagerEmail(),
                project.getSelectedCrew().getId()
        );
    }
}