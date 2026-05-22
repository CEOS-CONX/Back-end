package com.conx.server.user.dto;

import lombok.Getter;

@Getter
public enum UserRole {
    CREW("ROLE_CREW"), COMPANY("ROLE_COMPANY"), ADMIN("ROLE_ADMIN"), TEMPORAL("ROLE_TEMPORAL");

    private String role;

    UserRole(String role){
        this.role = role;
    }
}
