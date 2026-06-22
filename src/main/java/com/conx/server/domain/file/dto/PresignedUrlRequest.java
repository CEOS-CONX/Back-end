package com.conx.server.domain.file.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class PresignedUrlRequest {

    @NotBlank(message = "파일명은 필수입니다.")
    private final String fileName;

    @NotBlank(message = "파일 타입은 필수입니다.")
    private final String contentType;

    @JsonCreator
    public PresignedUrlRequest(
            @JsonProperty("fileName") String fileName,
            @JsonProperty("contentType") String contentType
    ) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
}