package com.conx.server.notification.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.user.domain.User;
import com.conx.server.user.service.common.UserFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final UserFinder userFinder;
    private final NotificationRepository notificationRepository;

    public NotificationService(UserFinder userFinder, NotificationRepository notificationRepository) {
        this.userFinder = userFinder;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationWrapperDTO> getAllNotifications(CustomUserDetails customUserDetails){
        User user = userFinder.findByEmail(customUserDetails.getUserEmail());

        List<Notification> notifications = notificationRepository.findAllByReceiverId(user.getId());
        return NotificationWrapperDTO.create(notifications);
    }

    @Transactional
    public void readNotification(long notificationId,
                                 CustomUserDetails customUserDetails){
        User user = userFinder.findByEmail(customUserDetails.getUserEmail());

        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, user.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND)
        );

        notification.read();
    }

    @Transactional
    public void readAll(CustomUserDetails customUserDetails){
        User user = userFinder.findByEmail(customUserDetails.getUserEmail());

        List<Notification> notifications = notificationRepository.findAllByReceiverId(user.getId());
        notifications.forEach(Notification::read);
    }

    @Transactional
    public void deleteNotification(long notificationId, CustomUserDetails userDetails){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND)
        );

        User user = userFinder.findByEmail(userDetails.getUserEmail());

        if (!(notification.getReceiverId() == user.getId())){
            throw new CustomException(ErrorCode.MISMATCH_NOTIFICATION_RECEIVER);
        }

        notificationRepository.delete(notification);
    }
}
