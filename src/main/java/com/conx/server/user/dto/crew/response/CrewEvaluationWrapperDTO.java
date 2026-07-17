package com.conx.server.user.dto.crew.response;

public record CrewEvaluationWrapperDTO(
        double overall,
        double completeness,
        double ability,
        double communication,
        double schedule,
        double recooperation
) {

    public CrewEvaluationWrapperDTO(
            Double overall,
            Double completeness,
            Double ability,
            Double communication,
            Double schedule,
            Double recooperation
    ) {
        this(
                valueOrZero(overall),
                valueOrZero(completeness),
                valueOrZero(ability),
                valueOrZero(communication),
                valueOrZero(schedule),
                valueOrZero(recooperation)
        );
    }

    private static double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }
}