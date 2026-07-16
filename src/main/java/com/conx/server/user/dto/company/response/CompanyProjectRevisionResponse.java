package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

/*
public record CompanyProjectRevisionResponse(
        Long projectId,
        Long submissionId,
        ProjectStatus projectStatus,
        ProjectSubmissionStatus submissionStatus,
        String revisionReason
) {

    public static CompanyProjectRevisionResponse of(
            Project project,
            ProjectSubmission submission
    ) {
        return new CompanyProjectRevisionResponse(
                project.getId(),
                submission.getId(),
                project.getStatus(),
                submission.getStatus(),
                submission.getRevisionReason()
        );
    }
}

 */