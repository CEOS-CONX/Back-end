package com.conx.server.notification.repository;

import com.conx.server.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiverId(long receiverId);

    Optional<Notification> findByIdAndReceiverId(long id, long receiverId);

    boolean existsByreceiverIdAndIsRead(long receiverId, boolean isRead);
}
