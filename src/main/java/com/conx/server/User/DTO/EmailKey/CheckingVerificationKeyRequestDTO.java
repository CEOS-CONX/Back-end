package com.conx.server.User.DTO.EmailKey;

public record CheckingVerificationKeyRequestDTO(
        String email,
        Integer code
) {

}
