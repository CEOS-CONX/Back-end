package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

import java.util.List;

public record CrewProjectSubmissionResponse(
        Long submissionId,
        Long projectId,
        String content,
        List<String> fileLinks,
        String revisionReason,
        ProjectSubmissionStatus status
) {
    public static CrewProjectSubmissionResponse from(ProjectSubmission submission) {
        return new CrewProjectSubmissionResponse(
                submission.getId(),
                submission.getProject().getId(),
                submission.getContent(),
                submission.getFileLinks(),
                submission.getRevisionReason(),
                submission.getStatus()
        );
    }
}