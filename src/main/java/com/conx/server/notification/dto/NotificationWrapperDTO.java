package com.conx.server.notification.dto;

import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import org.apache.tomcat.util.modeler.NotificationInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record NotificationWrapperDTO(
        long id,
        NotificationType type,
        String message,
        boolean isRead,

        LocalDateTime arriveTime,
        String sender
) {
    public static NotificationWrapperDTO create(Notification n){
        return new NotificationWrapperDTO(
                n.getId(),
                n.getType(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt(),
                n.getSender()
        );
    }

    public static List<NotificationWrapperDTO> create(List<Notification> ns){
        return ns.stream().map(NotificationWrapperDTO::create).toList();
    }
}
