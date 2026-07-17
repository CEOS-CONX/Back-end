package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Evaluation;

public record CrewEvaluationWrapperDTO(
        double overall,
        double completeness,
        double ability,
        double communication,
        double schedule,
        double reCooperation
) {

}