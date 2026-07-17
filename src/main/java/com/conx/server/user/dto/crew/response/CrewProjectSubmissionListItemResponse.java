package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.user.dto.crew.CrewSubmissionReplyStatus;

import java.time.LocalDateTime;

public record CrewProjectSubmissionListItemResponse(
        long submissionId,
        String title,
        String authorName,
        LocalDateTime submittedAt,
        ProjectSubmissionStatus submissionStatus,
        CrewSubmissionReplyStatus replyStatus
) {

    public static CrewProjectSubmissionListItemResponse from(
            ProjectSubmission submission
    ) {
        String authorName =
                submission.getCrew() == null
                        ? null
                        : submission.getCrew()
                        .getCrewName();

        return new CrewProjectSubmissionListItemResponse(
                submission.getId(),
                submission.getSubject(),
                authorName,
                submission.getResolvedSubmittedAt(),
                submission.getStatus(),
                CrewSubmissionReplyStatus.from(
                        submission.getStatus()
                )
        );
    }
}