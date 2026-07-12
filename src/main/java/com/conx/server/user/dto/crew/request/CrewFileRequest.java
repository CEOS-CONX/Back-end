package com.conx.server.user.dto.crew.request;

public record CrewFileRequest(
        String fileName,
        String extension,
        Long size,
        String url,
        String description
) {
}