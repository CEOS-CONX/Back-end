package com.conx.server.user.dto.crew.request;

public record ModifyCrewPortfolioRequestDTO(
        String imageLink,
        String name,
        String fileLink
) {
}
