package com.conx.server.user.dto.login.request;

public record LoginRequestDTO(
    String email,
    String password
) {
}
