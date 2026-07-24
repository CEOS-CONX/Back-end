package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private Long writerId;

    @Enumerated(EnumType.STRING)
    private UserRole writerRole;


    private String writerName;

    private String subject;

    @Lob
    private String content;

    private boolean secret;

    @Lob
    private String answerContent;

    private LocalDateTime answeredAt;

    private ProjectQuestion(
            Project project,
            Long writerId,
            UserRole writerRole,
            String writerName,
            String content,
            boolean secret,
            String subject
    ) {
        this.project = project;
        this.writerId = writerId;
        this.writerRole = writerRole;
        this.writerName = writerName;
        this.content = content;
        this.secret = secret;
        this.subject = subject;
    }

    public static ProjectQuestion create(
            Project project,
            Long writerId,
            UserRole writerRole,
            String writerName,
            String content,
            boolean secret,
            String subject
    ) {
        return new ProjectQuestion(project, writerId, writerRole, writerName, content, secret, subject);
    }

    public void answer(String answerContent) {
        this.answerContent = answerContent;
        this.answeredAt = LocalDateTime.now();
    }
}
