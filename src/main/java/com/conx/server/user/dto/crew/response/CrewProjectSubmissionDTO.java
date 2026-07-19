package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.AdditionalLinksWrapper;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CrewProjectSubmissionDTO(
        long submissionId,
        String title,
        List<String> fileLinks,
        List<AdditionalLinksWrapper> referenceLinks,
        String content,
        String authorName,
        ProjectSubmissionStatus status,
        LocalDateTime submittedAt,
        boolean editable
) {

    public static CrewProjectSubmissionDTO create(
            ProjectSubmission submission
    ) {
        if (submission == null) {
            return null;
        }

        String authorName =
                submission.getCrew() == null
                        ? null
                        : submission.getCrew()
                        .getCrewName();

        return new CrewProjectSubmissionDTO(
                submission.getId(),
                submission.getSubject(),
                safeList(
                        submission.getFileLinks()
                ),
                safeList(
                        submission.getAdditionalLinks()
                ),
                submission.getContent(),
                authorName,
                submission.getStatus(),
                submission.getResolvedSubmittedAt(),
                submission.isEditable()
        );
    }

    public List<String> textFileLinks() {
        return fileLinks;
    }

    public String crewReferenceInfo() {
        return content;
    }

    private static <T> List<T> safeList(
            List<T> values
    ) {
        return values == null
                ? List.of()
                : List.copyOf(values);
    }
}