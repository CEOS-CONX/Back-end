package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.CrewFile;

public record CrewFileResponse(
        Long fileId,
        String fileName,
        String extension,
        long size,
        String url,
        String description
) {

    public static CrewFileResponse from(CrewFile file) {
        return new CrewFileResponse(
                file.getId(),
                file.getFileName(),
                file.getExtension(),
                file.getSize(),
                file.getUrl(),
                file.getDescription()
        );
    }
}