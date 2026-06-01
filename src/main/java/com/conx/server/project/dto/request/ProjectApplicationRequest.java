package com.conx.server.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProjectApplicationRequest(

        @NotBlank(message = "자기소개를 입력해주세요.")
        String introduction,

        @NotBlank(message = "제안 내용을 입력해주세요.")
        String proposal
) {
}