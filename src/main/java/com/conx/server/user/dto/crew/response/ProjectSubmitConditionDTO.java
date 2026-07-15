package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.Project;

import java.util.Arrays;
import java.util.List;

public record ProjectSubmitConditionDTO(
        List<String> conditions
) {

    public static ProjectSubmitConditionDTO create(
            Project project
    ) {
        String requirement =
                project.getRequirement();

        if (
                requirement == null
                        || requirement.isBlank()
        ) {
            return new ProjectSubmitConditionDTO(
                    List.of()
            );
        }

        List<String> conditions =
                Arrays.stream(
                                requirement.split(",")
                        )
                        .map(String::trim)
                        .filter(value ->
                                !value.isBlank()
                        )
                        .toList();

        return new ProjectSubmitConditionDTO(
                conditions
        );
    }
}