package com.conx.server.user.dto.crew.request;

public record CrewLinkRequest(
        String name,
        String url,
        String description
) {
}