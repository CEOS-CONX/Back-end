package com.conx.server.user.dto.crew.response;

public record CrewEvaluationWrapperDTO(
        double mean,
        double completeness,
        double ability,
        double communication,
        double schedule,
        double reCooperation
) {
}