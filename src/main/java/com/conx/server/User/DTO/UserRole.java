package com.conx.server.User.DTO;

import lombok.Getter;

@Getter
public enum UserRole {
    CREW("ROLE_CREW"), COMPANY("ROLE_COMPANY"), ADMIN("ROLE_ADMIN");

    private String role;

    UserRole(String role){
        this.role = role;
    }
}
