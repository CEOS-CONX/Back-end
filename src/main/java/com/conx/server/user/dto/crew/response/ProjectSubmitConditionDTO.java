package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;

import java.util.Arrays;
import java.util.List;

public record ProjectSubmitConditionDTO(
        List<String> conditions
) {
    public static ProjectSubmitConditionDTO create(Project project){
        return new ProjectSubmitConditionDTO(Arrays.asList(project.getRequirement().split(",")));
    }
}
