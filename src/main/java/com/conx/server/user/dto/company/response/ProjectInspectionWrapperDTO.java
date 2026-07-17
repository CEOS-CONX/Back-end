package com.conx.server.user.dto.company.response;


public record ProjectInspectionWrapperDTO(
        DetailedProjectResponseDTO common,

        //결과물
        ProjectSubmissionWrapperDTO submission,
        //피드백
        ProjectFeedBackWrapperDTO feedBack
) {
    public static ProjectInspectionWrapperDTO from(
            DetailedProjectResponseDTO common,
            ProjectSubmissionWrapperDTO submission,
            ProjectFeedBackWrapperDTO feedBack
    ) {
        return new ProjectInspectionWrapperDTO(common, submission, feedBack);
    }
}
