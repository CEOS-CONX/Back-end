package com.conx.server.domain.file.controller;

import com.conx.server.domain.file.dto.PresignedUrlRequest;
import com.conx.server.domain.file.dto.PresignedUrlResponse;
import com.conx.server.domain.file.service.FileService;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 파일을 업로드용 PresignedUrl을 발급합니다.
     * @param request 파일명 + 파일타입 정보
     */
    @Operation(
            summary = "파일 업로드용 Presigned URL 발급",
            description = "로그인 사용자가 S3 PUT 업로드용 URL을 발급받습니다. fileName과 contentType은 필수이며 URL은 10분간 유효하고 업로드 시 동일한 Content-Type 헤더를 사용해야 합니다."
    )
    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponse> createPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = fileService.createPresignedUrl(request);
        return apiResponseFactory.success("Presigned URL 발급에 성공했습니다.", response, null);
    }
}
