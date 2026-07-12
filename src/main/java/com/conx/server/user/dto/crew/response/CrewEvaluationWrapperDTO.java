package com.conx.server.user.dto.crew.response;

public record CrewEvaluationWrapperDTO(
        double mean,
        double completeness,
        double ability,
        double communication,
        double schedule,
        double reCooperation
) {

    public CrewEvaluationWrapperDTO(
            Double mean,
            Double completeness,
            Double ability,
            Double communication,
            Double schedule,
            Double reCooperation
    ) {
        this(
                valueOrZero(mean),
                valueOrZero(completeness),
                valueOrZero(ability),
                valueOrZero(communication),
                valueOrZero(schedule),
                valueOrZero(reCooperation)
        );
    }

    private static double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }
}