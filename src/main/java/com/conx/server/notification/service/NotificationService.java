package com.conx.server.notification.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import com.conx.server.notification.dto.NotificationFilter;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.user.domain.User;
import com.conx.server.user.service.common.UserFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<NotificationWrapperDTO> getAllNotifications(CustomUserDetails customUserDetails,
                                                            NotificationFilter filter){
        User user = userFinder.findByEmail(customUserDetails.getUserEmail());

        List<Notification> notifications = new ArrayList<>();

        if (filter.equals(NotificationFilter.ALL)){
            notifications = notificationRepository.findAllByReceiverId(user.getId());
        } else if (filter.equals(NotificationFilter.PROJECT)){
            notifications = notificationRepository.findAllByReceiverIdAndTypeIn(user.getId(),
                    List.of(
                            NotificationType.ADJUSTMENT_DONE,
                            NotificationType.LATE_FOR_SUBMIT_DEADLINE,
                            NotificationType.CLOSE_TO_END_OF_RECRUITING,
                            NotificationType.PROJECT_CLOSE_TO_END,
                            NotificationType.PROJECT_REJECTED,
                            NotificationType.PROJECT_SELECTED,
                            NotificationType.RESULT_UPLOAD_CLOSE_TO_END,
                            NotificationType.RESULT_UPLOADED
                    ));
        } else if (filter.equals(NotificationFilter.PROJECT_QUESTION_ANSWER)){
            notifications = notificationRepository.findAllByReceiverIdAndTypeIn(user.getId(),
                    List.of(
                            NotificationType.QUESTION_REGISTERED,
                            NotificationType.QUESTION_ANSWER_REGISTERED
                    ));
        }

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
