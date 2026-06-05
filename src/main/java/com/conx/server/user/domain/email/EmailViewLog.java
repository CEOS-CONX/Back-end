package com.conx.server.user.domain.email;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.email.EmailTargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "email_view_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailViewLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long viewerId;

    @Enumerated(EnumType.STRING)
    private UserRole viewerRole;

    private String viewerEmail;

    @Enumerated(EnumType.STRING)
    private EmailTargetType targetType;

    private Long targetId;

    private String targetName;

    private String viewedEmail;

    private boolean consentAgreed;

    private String consentText;

    private EmailViewLog(
            Long viewerId,
            UserRole viewerRole,
            String viewerEmail,
            EmailTargetType targetType,
            Long targetId,
            String targetName,
            String viewedEmail,
            boolean consentAgreed,
            String consentText
    ) {
        this.viewerId = viewerId;
        this.viewerRole = viewerRole;
        this.viewerEmail = viewerEmail;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetName = targetName;
        this.viewedEmail = viewedEmail;
        this.consentAgreed = consentAgreed;
        this.consentText = consentText;
    }

    public static EmailViewLog create(
            Long viewerId,
            UserRole viewerRole,
            String viewerEmail,
            EmailTargetType targetType,
            Long targetId,
            String targetName,
            String viewedEmail,
            boolean consentAgreed,
            String consentText
    ) {
        return new EmailViewLog(
                viewerId,
                viewerRole,
                viewerEmail,
                targetType,
                targetId,
                targetName,
                viewedEmail,
                consentAgreed,
                consentText
        );
    }
}