package com.conx.server.domain.file.dto;

public class PresignedUrlResponse {

    private final String uploadUrl;
    private final String fileUrl;
    private final String fileKey;

    private PresignedUrlResponse(String uploadUrl, String fileUrl, String fileKey) {
        this.uploadUrl = uploadUrl;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
    }

    public static PresignedUrlResponse of(String uploadUrl, String fileUrl, String fileKey) {
        return new PresignedUrlResponse(uploadUrl, fileUrl, fileKey);
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileKey() {
        return fileKey;
    }
}