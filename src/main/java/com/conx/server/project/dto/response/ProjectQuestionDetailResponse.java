package com.conx.server.project.dto.response;

import com.conx.server.project.domain.ProjectQuestion;
import com.conx.server.user.dto.UserRole;

import java.time.LocalDateTime;

public record ProjectQuestionDetailResponse(
        Long questionId,
        Long projectId,
        Long writerId,
        UserRole writerRole,
        String writerName,
        String content,
        boolean secret,
        String answerContent,
        LocalDateTime answeredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProjectQuestionDetailResponse from(ProjectQuestion question) {
        return new ProjectQuestionDetailResponse(
                question.getId(),
                question.getProject().getId(),
                question.getWriterId(),
                question.getWriterRole(),
                question.getWriterName(),
                question.getContent(),
                question.isSecret(),
                question.getAnswerContent(),
                question.getAnsweredAt(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
