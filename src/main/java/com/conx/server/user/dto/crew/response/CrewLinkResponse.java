package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.CrewLink;

public record CrewLinkResponse(
        Long linkId,
        String name,
        String url,
        String description
) {

    public static CrewLinkResponse from(CrewLink link) {
        return new CrewLinkResponse(
                link.getId(),
                link.getName(),
                link.getUrl(),
                link.getDescription()
        );
    }

    public static CrewLinkResponse legacy(
            String name,
            String url
    ) {
        return new CrewLinkResponse(
                null,
                name,
                url,
                null
        );
    }
}