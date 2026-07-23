package com.conx.server.notification.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.dto.NotificationFilter;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
    @Operation(
            summary = "전체 알림 읽음 처리",
            description = "로그인 사용자가 수신한 모든 읽지 않은 알림을 읽음 처리합니다. COMPANY, CREW, ADMIN 사용자가 호출할 수 있습니다."
    )
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
    @Operation(
            summary = "특정 알림 읽음 처리",
            description = "로그인 사용자가 수신한 알림을 읽음 처리합니다. 본인 소유 알림만 처리할 수 있으며 이미 읽은 알림도 성공합니다."
    )
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
    @Operation(
            summary = "알림 삭제",
            description = "로그인 사용자가 수신한 특정 알림을 삭제합니다. 본인 소유 알림만 삭제할 수 있으며 삭제된 알림은 목록에서 제거됩니다."
    )
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
    @Operation(
            summary = "알림 목록 조회",
            description = "로그인 사용자가 수신한 알림을 필터별로 조회합니다. filter는 필수이며 ALL, PROJECT, PROJECT_QUESTION_ANSWER 중 하나를 전달해야 합니다."
    )
    @GetMapping
    public ApiResponse<List<NotificationWrapperDTO>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam NotificationFilter filter
    ){
        List<NotificationWrapperDTO> notifications = notificationService.getAllNotifications(customUserDetails, filter);
        return apiResponseFactory.success(notifications, customUserDetails);
    }
}
