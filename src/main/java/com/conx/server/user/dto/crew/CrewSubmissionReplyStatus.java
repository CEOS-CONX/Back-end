package com.conx.server.user.dto.crew;

import com.conx.server.project.domain.enums.ProjectSubmissionStatus;

public enum CrewSubmissionReplyStatus {

    WAITING_FOR_REPLY,
    REPLIED;

    public static CrewSubmissionReplyStatus from(
            ProjectSubmissionStatus status
    ) {
        if (
                status
                        == ProjectSubmissionStatus.SUBMITTED
        ) {
            return WAITING_FOR_REPLY;
        }

        return REPLIED;
    }
}