package com.conx.server.project.dto.response;

import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectType;

import java.time.LocalDateTime;

public record ProjectApplicationWrapperDTO(
        long projectId,
        long applicationId,

        ProjectType projectType,
        LocalDateTime applyDate,
        ProjectApplicationStatus status
) {
}
