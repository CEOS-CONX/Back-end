package com.conx.server.user.dto.login.response;

import com.conx.server.user.dto.UserRole;

public record LoginServiceResponseDTO (
    String accessToken,
    String refreshToken,
    boolean hasFullInfo,

    long userId,
    String email,
    UserRole userType
) {
    public static LoginServiceResponseDTO create(String accessToken, String refreshToken,
                                                 boolean hasFullInfo, long userId, String email, UserRole userType){
        return new LoginServiceResponseDTO(accessToken, refreshToken, hasFullInfo, userId, email, userType);
    }
}
