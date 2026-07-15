package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.user.dto.crew.CrewSubmissionReplyStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CrewProjectSubmissionDetailResponse(
        long submissionId,
        long projectId,
        String title,
        String authorName,
        LocalDateTime submittedAt,
        ProjectSubmissionStatus submissionStatus,
        CrewSubmissionReplyStatus replyStatus,
        String content,
        List<String> fileLinks,
        List<String> referenceLinks,
        String revisionReason
) {

    public static CrewProjectSubmissionDetailResponse from(
            ProjectSubmission submission
    ) {
        String authorName =
                submission.getCrew() == null
                        ? null
                        : submission.getCrew()
                        .getCrewName();

        return new CrewProjectSubmissionDetailResponse(
                submission.getId(),
                submission.getProject().getId(),
                submission.getTitle(),
                authorName,
                submission.getResolvedSubmittedAt(),
                submission.getStatus(),
                CrewSubmissionReplyStatus.from(
                        submission.getStatus()
                ),
                submission.getContent(),
                safeList(
                        submission.getFileLinks()
                ),
                safeList(
                        submission.getReferenceLinks()
                ),
                submission.getRevisionReason()
        );
    }

    private static List<String> safeList(
            List<String> values
    ) {
        return values == null
                ? List.of()
                : List.copyOf(values);
    }
}