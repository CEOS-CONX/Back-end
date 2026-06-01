package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.ProjectSubmission;

import java.util.List;

public record CrewProjectSubmissionDTO(
        long submissionId,

        List<String> textFileLinks,
        String crewReferenceInfo
) {
    public static CrewProjectSubmissionDTO create(ProjectSubmission projectSubmission){
        if (projectSubmission == null) return null;
        return new CrewProjectSubmissionDTO(projectSubmission.getId(),
                projectSubmission.getFileLinks(), projectSubmission.getContent());
    }
}
