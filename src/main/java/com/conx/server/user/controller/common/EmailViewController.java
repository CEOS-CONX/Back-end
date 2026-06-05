package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.email.EmailViewRequest;
import com.conx.server.user.dto.email.EmailViewResponse;
import com.conx.server.user.service.common.EmailViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailViewController {

    private final EmailViewService emailViewService;

    @PostMapping("/api/v1/email-views")
    public ApiResponse<EmailViewResponse> viewEmail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EmailViewRequest request
    ) {
        EmailViewResponse response = emailViewService.viewEmail(
                customUserDetails,
                request
        );

        return ApiResponse.success("이메일 조회에 성공했습니다.", response);
    }
}