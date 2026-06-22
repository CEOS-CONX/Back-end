package com.conx.server.user.dto.login.response;

public record TokenReissueResponseDTO(
        String accessToken,
        String refreshToken
) {
}
