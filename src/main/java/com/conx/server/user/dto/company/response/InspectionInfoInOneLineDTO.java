package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

import java.time.LocalDate;

public record InspectionInfoInOneLineDTO(
        long inspectionId,
        ProjectSubmissionStatus inspectionStatus,
        String inspectionName,
        String writer,
        LocalDate registerDate
) {
    public static InspectionInfoInOneLineDTO create(ProjectSubmission submission){
        return new InspectionInfoInOneLineDTO(submission.getId(), submission.getStatus(), submission.getSubject(), submission.getWriter(), submission.getCreatedAt().toLocalDate());
    }
}
