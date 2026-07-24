package com.conx.server.user.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User extends BaseEntity {

    protected User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = UserRole.TEMPORAL;
        this.status = UserStatus.PENDING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    protected void activate(UserRole role) {
        this.status = UserStatus.ACTIVE;
        this.role = role;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void withdrawUser() {
        this.status = UserStatus.WITHDRAW;
    }

    public boolean isLoginable() {
        this.status = UserStatus.ACTIVE;
    }
}