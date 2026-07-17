package com.conx.server.user.dto.crew.response;

public record CrewProjectInfoDTO(
        long appliedProjectAmount,
        long progressProjectAmount,
        long executionCompletedProjectAmount,
        long submissionCompletedProjectAmount,
        long settlementCompletedProjectAmount
) {
}