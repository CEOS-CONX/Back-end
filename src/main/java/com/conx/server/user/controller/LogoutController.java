package com.conx.server.user.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.service.login_logout.LogoutService;
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

    /**
     * 로그아웃합니다.
     * @param userDetails 자동으로 주입되는 현재 로그인 중인 사용자 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        logoutService.logout(userDetails);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
