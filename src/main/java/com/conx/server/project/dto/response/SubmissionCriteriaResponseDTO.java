package com.conx.server.project.dto.response;

import com.conx.server.project.domain.ProjectSubmissionCriteria;
import jakarta.persistence.*;

public record SubmissionCriteriaResponseDTO(
        Long id,

        String finalResult,

        int numberOfResult,

        boolean done
) {
    public static SubmissionCriteriaResponseDTO from(ProjectSubmissionCriteria c){
        return new SubmissionCriteriaResponseDTO(c.getId(), c.getFinalResult(), c.getNumberOfResult(), c.isDone());
    }
}
