package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.service.login_logout.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logout")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LogoutController {

    private final LogoutService logoutService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 로그아웃합니다.
     * @param userDetails 자동으로 주입되는 현재 로그인 중인 사용자 정보
     */
    @Operation(
            summary = "로그아웃",
            description = "로그인 사용자의 Redis refresh token을 삭제하여 추가 토큰 재발급을 차단합니다. 클라이언트의 refreshToken 쿠키와 기존 access token은 서버에서 직접 삭제하거나 즉시 무효화하지 않습니다."
    )
    @GetMapping
    public ApiResponse<?> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        logoutService.logout(userDetails);
        return apiResponseFactory.success(null);
    }
}
