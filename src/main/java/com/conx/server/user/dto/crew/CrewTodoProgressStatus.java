package com.conx.server.user.dto.crew;

import com.conx.server.project.domain.enums.CrewProjectTodoStatus;

public enum CrewTodoProgressStatus {

    NEEDS_CONFIRMATION,
    IN_PROGRESS,
    COMPLETED;

    public CrewProjectTodoStatus toDomainStatus() {
        return CrewProjectTodoStatus.valueOf(
                this.name()
        );
    }

    public static CrewTodoProgressStatus from(
            CrewProjectTodoStatus status
    ) {
        return CrewTodoProgressStatus.valueOf(
                status.name()
        );
    }
}