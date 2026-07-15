package com.conx.server.user.dto.crew.response;

import com.conx.server.project.domain.CrewProjectTodo;
import com.conx.server.project.domain.Project;
import com.conx.server.user.dto.crew.CrewTodoProgressStatus;

import java.time.LocalDateTime;

public record CrewTodoProjectResponse(
        long todoId,
        long projectId,
        String taskName,
        CrewTodoProgressStatus progressStatus,
        String projectName,
        String brandName,
        LocalDateTime registeredAt
) {

    public static CrewTodoProjectResponse from(
            CrewProjectTodo todo
    ) {
        Project project =
                todo.getProject();

        return new CrewTodoProjectResponse(
                todo.getId(),
                project.getId(),
                todo.getTaskName(),
                CrewTodoProgressStatus.from(
                        todo.getStatus()
                ),
                project.getName(),
                project.getBrandName(),
                todo.getCreatedAt()
        );
    }
}