package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitProjectResultRequestDTO(
        @NotEmpty(message = "결과물을 한 개 이상 제출해주세요")
        List<String> fileLinks,
        String content
) {
}
