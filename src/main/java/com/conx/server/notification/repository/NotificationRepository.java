package com.conx.server.notification.repository;

import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiverId(long receiverId);

    Optional<Notification> findByIdAndReceiverId(long id, long receiverId);

    boolean existsByreceiverIdAndIsRead(long receiverId, boolean isRead);

    List<Notification> findAllByReceiverIdAndTypeIn(long receiverId, Collection<NotificationType> types);
}
