package com.conx.server.user.dto.crew.response;

import com.conx.server.project.dto.response.TodoProjectInfoDTO;

import java.util.List;

public record CrewDashboardResultDTO(
    int totalSubsidy,

    CrewEvaluationWrapperDTO evaluation,
    CrewProjectInfoDTO projectInfo,

    List<TodoProjectInfoDTO> todoProjects
) {
}