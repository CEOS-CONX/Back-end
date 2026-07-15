package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CrewProjectSubmissionDTO(
        long submissionId,
        String title,
        List<String> fileLinks,
        List<String> referenceLinks,
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
                submission.getTitle(),
                submission.getFileLinks(),
                submission.getReferenceLinks(),
                submission.getContent(),
                authorName,
                submission.getStatus(),
                submission.getCreatedAt(),
                submission.isEditable()
        );
    }

    /**
     * 기존 응답 코드 호환용
     */
    public List<String> textFileLinks() {
        return fileLinks;
    }

    /**
     * 기존 응답 코드 호환용
     */
    public String crewReferenceInfo() {
        return content;
    }
}