package com.conx.server.user.dto.emailKey;

public record CheckingVerificationKeyRequestDTO(
        String email,
        Integer code
) {

}
