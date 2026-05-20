package com.conx.server.user.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.domain.types.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    private User(String email, String password,
                 boolean allowCollectingPersonalInformation, boolean allowSendingPromoteMessage){
        this.email = email;
        this.password = password;
        this.status = UserStatus.PENDING;
        this.allowCollectingPersonalInformation = allowCollectingPersonalInformation;
        this.allowSendingPromoteMessage = allowSendingPromoteMessage;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    protected String email;

    protected String password;

    @Enumerated(EnumType.STRING)
    protected UserStatus status;

    protected boolean allowCollectingPersonalInformation;

    protected boolean allowSendingPromoteMessage;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static User create (String email, String password,
                               boolean allowCollectingPersonalInformation, boolean allowSendingPromoteMessage){
        return new User(email, password, allowCollectingPersonalInformation, allowSendingPromoteMessage);
    }

    public void updateUser(UserRole role){
        this.status = UserStatus.ACTIVE;
        this.role = role;
    }

    public void withdrawUser(){
        this.status = UserStatus.WITHDRAW;
    }
}
