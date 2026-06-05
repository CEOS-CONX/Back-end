package com.conx.server.global.common;

import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApiResponseFactory {

    private final NotificationRepository notificationRepository;

    public ApiResponseFactory(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private Boolean findUnReadNotificationByUser(CustomUserDetails user){
        if (user == null){
            return null;
        } else {
            return notificationRepository.existsByreceiverIdAndIsRead(user.getId(), false);
        }
    }

    @Transactional(readOnly = true)
    public <T> ApiResponse<T> success(String message, T payload, CustomUserDetails user){
        return ApiResponse.success(message, payload, findUnReadNotificationByUser(user));
    }

    @Transactional(readOnly = true)
    public <T> ApiResponse<T> success(T payload, CustomUserDetails user){
        return ApiResponse.success(payload, findUnReadNotificationByUser(user));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> success(CustomUserDetails user){
        return ApiResponse.success(findUnReadNotificationByUser(user));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> success(String message, CustomUserDetails user){
        return ApiResponse.success(message, findUnReadNotificationByUser(user));
    }
}