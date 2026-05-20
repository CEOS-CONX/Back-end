package com.conx.server.user.dto.loginRequest;

public record LoginRequestDTO(
    String email,
    String password
) {
}
