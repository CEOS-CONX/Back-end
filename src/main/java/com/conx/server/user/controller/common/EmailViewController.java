package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.email.EmailViewRequest;
import com.conx.server.user.dto.email.EmailViewResponse;
import com.conx.server.user.service.common.EmailViewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailViewController {

    private final EmailViewService emailViewService;
    private final ApiResponseFactory apiResponseFactory;

    @Operation(
            summary = "연락 이메일 조회",
            description = "로그인한 COMPANY 또는 CREW가 consentAgreed=true로 동의한 뒤 크루 또는 프로젝트 등록 기업의 연락 이메일을 조회합니다. targetType은 CREW 또는 PROJECT이며 조회할 때마다 조회자와 대상 이메일 정보가 이력으로 저장됩니다."
    )
    @PostMapping("/api/v1/email-views")
    public ApiResponse<EmailViewResponse> viewEmail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EmailViewRequest request
    ) {
        EmailViewResponse response = emailViewService.viewEmail(
                customUserDetails,
                request
        );

        return apiResponseFactory.success("이메일 조회에 성공했습니다.", response, customUserDetails);
    }
}
