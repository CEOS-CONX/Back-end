package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.crew.Evaluation;

import java.time.LocalDateTime;

public record CompanyProjectEvaluationResponse(
        Long evaluationId,
        Long projectId,
        Long crewId,
        Long companyId,
        int completeness,
        int schedule,
        int ability,
        int recooperation,
        int communication,
        double mean,
        LocalDateTime createdAt
) {

    public static CompanyProjectEvaluationResponse from(
            Evaluation evaluation
    ) {
        return new CompanyProjectEvaluationResponse(
                evaluation.getId(),
                evaluation.getProject().getId(),
                evaluation.getCrew().getId(),
                evaluation.getCompany().getId(),
                evaluation.getCompleteness(),
                evaluation.getSchedule(),
                evaluation.getAbility(),
                evaluation.getReCooperation(),
                evaluation.getCommunication(),
                evaluation.getMean(),
                evaluation.getCreatedAt()
        );
    }
}