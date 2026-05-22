package com.conx.server.user.controller;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.service.LogoutService;
import lombok.AccessLevel;
import lombok.Getter;
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

    @GetMapping
    public ResponseEntity<ApiResponse<?>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        logoutService.logout(userDetails);
        return ResponseEntity.ok(ApiResponse.ofEmpty());
    }
}
