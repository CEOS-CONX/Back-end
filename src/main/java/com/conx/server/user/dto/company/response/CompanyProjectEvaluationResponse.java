package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.crew.Evaluation;

import java.time.LocalDateTime;

public record CompanyProjectEvaluationResponse(
        Long crewId,
        int completeness,
        int schedule,
        int ability,
        int reCooperation,
        int communication,
        double overall
) {

    public static CompanyProjectEvaluationResponse from(
            Evaluation evaluation
    ) {
        return new CompanyProjectEvaluationResponse(
                evaluation.getId(),

        );
    }
}