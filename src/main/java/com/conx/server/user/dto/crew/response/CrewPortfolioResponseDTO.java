package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Portfolio;

public record CrewPortfolioResponseDTO(
        long id,
        String imageLink,
        String name,
        String description
) {

    public static CrewPortfolioResponseDTO create(
            Portfolio portfolio
    ) {
        return new CrewPortfolioResponseDTO(
                portfolio.getId(),
                portfolio.getThumbnailImageLink(),
                portfolio.getPortfolioName(),
                portfolio.getDescription()
        );
    }
}