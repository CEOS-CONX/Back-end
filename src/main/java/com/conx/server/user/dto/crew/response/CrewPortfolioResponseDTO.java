package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Portfolio;

public record CrewPortfolioResponseDTO(
        long id,
        String imageLink,
        String name,
        String fileLink
) {
    public static CrewPortfolioResponseDTO create(Portfolio p) {
        return new CrewPortfolioResponseDTO(p.getId(), p.getImageLink(), p.getPortfolioName(), p.getFileLink());
    }
}
