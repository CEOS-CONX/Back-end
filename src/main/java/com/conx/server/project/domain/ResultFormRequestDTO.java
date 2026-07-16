package com.conx.server.project.domain;

public record ResultFormRequestDTO(
        String platform,

        String contentType,

        int numberOfResult,

        String finalResult
) {
}
