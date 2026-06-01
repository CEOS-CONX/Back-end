package com.conx.server.notification.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    //전체 알림 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<?>> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        notificationService.readAll(customUserDetails);
        return ResponseEntity.ok(ApiResponse.success());
    }


    //알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<?>> readNotification(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long notificationId
    ){
        notificationService.readNotification(notificationId, customUserDetails);
        return ResponseEntity.ok(ApiResponse.success());
    }

    //알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<?>> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long notificationId
    ){
        notificationService.deleteNotification(notificationId, customUserDetails);
        return ResponseEntity.ok(ApiResponse.success());
    }

    //알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationWrapperDTO>>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<NotificationWrapperDTO> notifications = notificationService.getAllNotifications(customUserDetails);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
}
