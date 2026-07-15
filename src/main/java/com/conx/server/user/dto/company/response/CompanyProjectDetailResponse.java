package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.response.ResultFormResponse;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;

import java.time.LocalDate;
import java.util.List;

public record CompanyProjectDetailResponse(
        long projectId,
        ProjectStatus projectStatus,
        String projectName,
        String brandName,
        String managerName,
        String managerEmail,

        Crew crew,
        String crewImageLink,
        String crewName,
        CrewType crewType,

        //진행단계
        LocalDate crewSelectedDate,
        LocalDate projectStartDate,
        LocalDate projectEndDate,
        LocalDate submissionDate,
        LocalDate endDate,

        //제출기준
        List<ResultFormResponse> resultForm
) {
    public static CompanyProjectDetailResponse create(Project project){
        LocalDate crewSelectedDate = project.getCrewSelectedDate();
        LocalDate projectStartDate = project.getProjectStartDate();
        LocalDate projectEndDate = project.getProjectDeadline();
        LocalDate submissionDate = project.getSubmitDeadline() != null ? project.getSubmitDeadline() : project.getResultSubmittedDate();
        LocalDate endDate = project.getProjectEndedDate();

        return new CompanyProjectDetailResponse(project.getId(),
                project.getStatus(),
                project.getProjectName(),
                project.getBrandName(),
                project.getManagerName(),
                project.getManagerEmail(),

                project.getSelectedCrew(),
                project.getSelectedCrew().getProfileImage(),
                project.getCrewName(),
                project.getCrewType(),

                crewSelectedDate, projectStartDate, projectEndDate, submissionDate, endDate,
                project.getResultForm().stream().map(ResultFormResponse::from).toList()
        );
    }
}
