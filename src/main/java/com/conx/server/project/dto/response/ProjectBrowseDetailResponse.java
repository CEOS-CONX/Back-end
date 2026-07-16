package com.conx.server.project.dto.response;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.project.domain.AdditionalLinksWrapper;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record ProjectBrowseDetailResponse(
        boolean isImminent,
        int dayBeforeDeadline,

        Long projectId,
        List<String> projectImage,
        String projectName,
        String projectExplanation,
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
        int peopleNumber,
        String competency,
        String preferenceCondition,

        List<ResultFormResponse> resultForm,

        long subsidy,
        boolean incentive,
        String incentiveCondition,

        List<FileResponseDTO> files,
        List<AdditionalLinksWrapper> links,

        int views
) {
    public static ProjectBrowseDetailResponse from(Project project,
                                                   List<FileResponseDTO> files) {
        int dayBeforeDeadline = (int) ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getRecruitDeadLine()
        );

        return new ProjectBrowseDetailResponse(
                dayBeforeDeadline >= 0 && dayBeforeDeadline <= 3,
                dayBeforeDeadline,

                project.getId(),
                project.getProjectImage(),
                project.getProjectName(),
                project.getProjectExplanation(),
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
                project.getPeopleNumber(),
                project.getCompetency(),
                project.getPreferenceCondition(),

                project.getResultForm().stream().map(ResultFormResponse::from).toList(),

                project.getSubsidy(),
                project.isIncentive(),
                project.getIncentiveCondition(),

                files,
                project.getLinks(),
                project.getViews()
        );
    }
}