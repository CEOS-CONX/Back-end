package com.conx.server.user.dto.crew.response;

import com.conx.server.project.dto.response.DetailedProjectWrapperForCrewWorkSpaceDTO;

public record CrewProjectWorkSpaceDTO(
        boolean isEditable,
        DetailedProjectWrapperForCrewWorkSpaceDTO project,
        CrewProjectSubmissionDTO submit,
        ProjectSubmitConditionDTO submitCondition
) {
}
