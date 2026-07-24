package com.conx.server.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProjectQuestionCreateRequest(

        @NotBlank(message = "질문 제목을 입력해주세요.")
        String subject,

        @NotBlank(message = "질문 내용을 입력해주세요.")
        String content,

        boolean secret
) {
}
