package com.conx.server.domain.file.dto;

public record FileRequestDTO (
        String originalName,
        String fileLinks,
        String explanation
) {
}
