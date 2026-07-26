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
    private Notification(
            NotificationType type,
            long receiverId,
            String message,
            String sender,
            Long projectId,
            Long questionId,
            Long applicationId,
            Long submissionId,
            Long settlementId
    ){
        this.type = type;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.sender = sender;
        this.projectId = projectId;
        this.questionId = questionId;
        this.applicationId = applicationId;
        this.submissionId = submissionId;
        this.settlementId = settlementId;
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

    private Long projectId;

    private Long questionId;

    private Long applicationId;

    private Long submissionId;

    private Long settlementId;

    public static Notification create(
            NotificationType type,
            long receiverId,
            String message,
            String sender
    ) {
        return new Notification(
                type,
                receiverId,
                message,
                sender,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static Notification create(
            NotificationType type,
            long receiverId,
            String message,
            String sender,
            Long projectId,
            Long questionId,
            Long applicationId,
            Long submissionId,
            Long settlementId
    ) {
        return new Notification(
                type,
                receiverId,
                message,
                sender,
                projectId,
                questionId,
                applicationId,
                submissionId,
                settlementId
        );
    }

    public void read(){
        if (isRead) return;

        this.isRead = true;
        this.readTime = LocalDateTime.now();
    }
}
