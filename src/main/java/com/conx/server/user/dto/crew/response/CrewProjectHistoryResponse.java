package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.response.ResultFormResponse;

import java.time.LocalDate;
import java.util.List;

public record CrewProjectHistoryResponse(
        Long projectId,
        ProjectStatus status,
        String projectName,
        String brandName,
        ProjectType projectType,
        List<ResultFormResponse> resultForm,
        Double point,
        LocalDate projectStartDate,
        LocalDate projectDeadline
) {

    public static CrewProjectHistoryResponse from(
            Project project,
            Double point
    ) {
        List<ResultFormResponse> resultForm =
                project.getResultForm() == null
                        ? List.of()
                        : project.getResultForm()
                        .stream()
                        .map(ResultFormResponse::from)
                        .toList();

        return new CrewProjectHistoryResponse(
                project.getId(),
                project.getStatus(),
                project.getProjectName(),
                project.getBrandName(),
                project.getProjectType(),
                resultForm,
                point,
                project.getProjectStartDate(),
                project.getProjectDeadline()
        );
    }
}
