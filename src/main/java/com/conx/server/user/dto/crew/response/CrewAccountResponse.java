package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Crew;

public record CrewAccountResponse(
        String name,
        String email,
        String representativePhone,
        String representativeEmail
) {

    public static CrewAccountResponse from(Crew crew) {
        return new CrewAccountResponse(
                crew.getManagerName(),
                crew.getEmail(),
                crew.getManagerPhoneNumber(),
                crew.getRepresentativeEmail()
        );
    }
}
