package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Portfolio;

import java.time.LocalDateTime;

public record CrewPortfolioItemResponse(
        long portfolioId,
        String imageUrl,
        String name,
        String description,
        String fileUrl,
        LocalDateTime createdAt
) {

    public static CrewPortfolioItemResponse from(
            Portfolio portfolio
    ) {
        return new CrewPortfolioItemResponse(
                portfolio.getId(),
                portfolio.getThumbnailImageLink(),
                portfolio.getPortfolioName(),
                portfolio.getDescription(),
                portfolio.getPdfLink(),
                portfolio.getCreatedAt()
        );
    }
}