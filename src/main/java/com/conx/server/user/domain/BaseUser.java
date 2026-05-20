package com.conx.server.user.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.domain.types.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseUser extends BaseEntity {

    protected String email;

    protected String password;

    @Enumerated(EnumType.STRING)
    protected UserStatus status;

    protected String managerName;

    protected String job;

    protected String profileImage;

    protected boolean allowCollectingPersonalInformation;

    protected boolean allowSendingPromoteMessage;

    public void completeSignup() {
        this.status = UserStatus.ACTIVE;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAW;
    }

    protected BaseUser(
            String email,
            String password,
            boolean allowCollectingPersonalInformation,
            boolean allowSendingPromoteMessage
    ) {
        this.email = email;
        this.password = password;
        this.status = UserStatus.PENDING;
        this.allowCollectingPersonalInformation = allowCollectingPersonalInformation;
        this.allowSendingPromoteMessage = allowSendingPromoteMessage;
    }
}
