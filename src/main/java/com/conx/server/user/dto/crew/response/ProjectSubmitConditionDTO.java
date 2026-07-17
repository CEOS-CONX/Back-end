package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.dto.response.ResultFormResponse;

import java.util.List;

public record ProjectSubmitConditionDTO(
        List<ResultFormResponse> conditions
) {

    public static ProjectSubmitConditionDTO create(
            Project project
    ) {
        if (
                project.getResultForm() == null
                        || project.getResultForm().isEmpty()
        ) {
            return new ProjectSubmitConditionDTO(
                    List.of()
            );
        }

        List<ResultFormResponse> conditions =
                project.getResultForm()
                        .stream()
                        .map(ResultFormResponse::from)
                        .toList();

        return new ProjectSubmitConditionDTO(
                conditions
        );
    }
}