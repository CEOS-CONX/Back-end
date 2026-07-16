package com.conx.server.domain.file.dto;

import com.conx.server.domain.file.domain.File;

public record FileResponseDTO(
        long fileId,
        String fileName,
        String extension,
        long size,
        String url,
        String explanation
) {
    public static FileResponseDTO from(File file){
        return new FileResponseDTO(
                file.getId(), file.getOriginalName(), file.getExtension(),
                file.getSize(), file.getUrl(), file.getExplanation()
        );
    }
}