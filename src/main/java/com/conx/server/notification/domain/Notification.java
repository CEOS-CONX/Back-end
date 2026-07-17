package com.conx.server.notification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    private Notification(NotificationType type, long receiverId, String message, String sender){
        this.type = type;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.sender = sender;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private long receiverId;

    private String message;

    private String sender;

    private LocalDateTime createdAt;

    private boolean isRead;

    private LocalDateTime readTime;

    public static Notification create(NotificationType type, long receiverId, String message, String sender) {
        return new Notification(type, receiverId, message, sender);
    }

    public void read(){
        if (isRead) return;

        this.isRead = true;
        this.readTime = LocalDateTime.now();
    }
}
