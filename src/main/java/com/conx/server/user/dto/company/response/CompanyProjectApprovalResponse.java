package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

public record CompanyProjectApprovalResponse(
        Long projectId,
        Long submissionId,
        ProjectStatus projectStatus,
        ProjectSubmissionStatus submissionStatus
) {

    public static CompanyProjectApprovalResponse of(
            Project project,
            ProjectSubmission submission
    ) {
        return new CompanyProjectApprovalResponse(
                project.getId(),
                submission.getId(),
                project.getStatus(),
                submission.getStatus()
        );
    }
}