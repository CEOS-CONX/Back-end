package com.conx.server.notification.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.notification.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationController {

    private final NotificationService notificationService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 전체 알림 읽음 처리하기
     * @param customUserDetails 인증정보
     */
    @PatchMapping("/read-all")
    public ApiResponse<?> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        notificationService.readAll(customUserDetails);
        return apiResponseFactory.success(customUserDetails);
    }


    /**
     * 특정 알림 읽을 처리
     * @param customUserDetails 인증정보
     * @param notificationId 읽을 알림id
     */
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<?> readNotification(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long notificationId
    ){
        notificationService.readNotification(notificationId, customUserDetails);
        return apiResponseFactory.success(customUserDetails);
    }

    /**
     * 알림 삭제하기
     * @param customUserDetails 인증정보
     * @param notificationId 삭제할 알림id
     */
    @DeleteMapping("/{notificationId}")
    public ApiResponse<?> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long notificationId
    ){
        notificationService.deleteNotification(notificationId, customUserDetails);
        return apiResponseFactory.success(customUserDetails);
    }

    /**
     * 알림 목록 조회하기
     * @param customUserDetails 인증정보
     */
    @GetMapping
    public ApiResponse<List<NotificationWrapperDTO>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<NotificationWrapperDTO> notifications = notificationService.getAllNotifications(customUserDetails);
        return apiResponseFactory.success(notifications, customUserDetails);
    }
}
