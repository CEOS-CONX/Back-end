package com.conx.server.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProjectQuestionAnswerRequest(

        @NotBlank(message = "답변 내용을 입력해주세요.")
        String answerContent
) {
}
