package com.conx.server.user.dto.crew.response;

import com.conx.server.project.dto.response.ProjectWrapperForCrewWorkSpaceDTO;

public record CrewProjectWorkSpaceDTO(
        ProjectWrapperForCrewWorkSpaceDTO project,
        CrewProjectSubmissionDTO submit,
        ProjectSubmitConditionDTO submitCondition
) {
}
