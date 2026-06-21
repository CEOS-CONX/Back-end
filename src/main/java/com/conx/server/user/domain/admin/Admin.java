package com.conx.server.user.domain.admin;

import com.conx.server.user.domain.User;
import com.conx.server.user.dto.UserRole;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends User {
    public Admin(String email, String password){
        super(email, password);
        super.activate(UserRole.ADMIN);
    }
}
