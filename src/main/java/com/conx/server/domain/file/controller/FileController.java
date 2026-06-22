package com.conx.server.domain.file.controller;

import com.conx.server.domain.file.dto.PresignedUrlRequest;
import com.conx.server.domain.file.dto.PresignedUrlResponse;
import com.conx.server.domain.file.service.FileService;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 파일을 업로드합니다
     * @param request 파일명 + 파일타입 정보
     */
    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponse> createPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = fileService.createPresignedUrl(request);
        return apiResponseFactory.success("Presigned URL 발급에 성공했습니다.", response, null);
    }
}