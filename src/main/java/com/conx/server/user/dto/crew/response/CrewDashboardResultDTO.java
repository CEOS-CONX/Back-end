package com.conx.server.user.dto.crew.response;

import java.util.List;

public record CrewDashboardResultDTO(
        long totalSubsidy,
        CrewEvaluationWrapperDTO evaluation,
        CrewProjectInfoDTO projectInfo,
        List<CrewTodoProjectResponse> todoProjects
) {
}