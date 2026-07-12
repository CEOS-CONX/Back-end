package com.conx.server.project.dto.response;

import com.conx.server.project.domain.ProjectQuestion;
import com.conx.server.user.dto.UserRole;

import java.time.LocalDateTime;

public record ProjectQuestionResponse(
        Long questionId,
        Long projectId,
        Long writerId,
        UserRole writerRole,
        String writerName,
        String content,
        boolean secret,
        boolean canView,
        boolean answered,
        LocalDateTime answeredAt,
        LocalDateTime createdAt
) {

    private static final String SECRET_CONTENT = "비밀글입니다.";

    public static ProjectQuestionResponse from(
            ProjectQuestion question,
            boolean canView
    ) {
        return new ProjectQuestionResponse(
                question.getId(),
                question.getProject().getId(),
                question.getWriterId(),
                question.getWriterRole(),
                question.getWriterName(),
                canView ? question.getContent() : SECRET_CONTENT,
                question.isSecret(),
                canView,
                question.getAnswerContent() != null,
                question.getAnsweredAt(),
                question.getCreatedAt()
        );
    }
}