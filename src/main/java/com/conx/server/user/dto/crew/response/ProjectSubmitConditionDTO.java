package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.dto.response.ResultFormResponse;

import java.util.Arrays;
import java.util.List;

public record ProjectSubmitConditionDTO(
        List<ResultFormResponse> conditions
) {
    public static ProjectSubmitConditionDTO create(Project project){
        return new ProjectSubmitConditionDTO(project.getResultForm().stream().map(ResultFormResponse::from).toList());
    }
}
