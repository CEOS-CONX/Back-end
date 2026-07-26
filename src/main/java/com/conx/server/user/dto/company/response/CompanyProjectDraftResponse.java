package com.conx.server.user.dto.company.response;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.response.AdditionalLinkResponse;
import com.conx.server.project.dto.response.ResultFormResponse;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.util.List;

public record CompanyProjectDraftResponse(
        Long projectId,
        List<String> projectImage,

        String brandName,
        String managerName,
        String managerEmail,

        String projectName,
        String projectExplanation,
        Industry industry,

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
        List<AdditionalLinkResponse> additionalLinks,

        ProjectStatus status
) {

    public static CompanyProjectDraftResponse from(
            Project project,
            List<FileResponseDTO> files
    ) {
        List<AdditionalLinkResponse> additionalLinks =
                project.getLinks() == null
                        ? List.of()
                        : project.getLinks().stream()
                        .map(AdditionalLinkResponse::from)
                        .toList();

        return new CompanyProjectDraftResponse(
                project.getId(),
                project.getProjectImage(),

                project.getBrandName(),
                project.getManagerName(),
                project.getManagerEmail(),

                project.getProjectName(),
                project.getProjectExplanation(),
                project.getIndustry(),

                project.getProjectType(),
                project.getResultForm().stream()
                        .map(ResultFormResponse::from)
                        .toList(),

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
                additionalLinks,

                project.getStatus()
        );
    }
}
