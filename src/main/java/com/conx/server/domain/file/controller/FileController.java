package com.conx.server.domain.file.controller;

import com.conx.server.domain.file.dto.PresignedUrlRequest;
import com.conx.server.domain.file.dto.PresignedUrlResponse;
import com.conx.server.domain.file.service.FileService;
import com.conx.server.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponse> createPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = fileService.createPresignedUrl(request);
        return ApiResponse.success("Presigned URL 발급에 성공했습니다.", response);
    }
}