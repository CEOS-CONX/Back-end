package com.conx.server.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProjectApplicationRequest(

        @NotBlank(message = "지원 동기를 입력해주세요.")
        String motivation
) {
}