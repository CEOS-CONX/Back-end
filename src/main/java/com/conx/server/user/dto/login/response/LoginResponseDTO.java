package com.conx.server.user.dto.login.response;

import com.conx.server.user.dto.UserRole;

public record LoginResponseDTO(
        boolean hasFullInfo,

        long userId,
        String email,
        UserRole userType
) {
    public static LoginResponseDTO create(LoginServiceResponseDTO res){
        return new LoginResponseDTO(res.hasFullInfo(), res.userId(), res.email(), res.userType());
    }
}
