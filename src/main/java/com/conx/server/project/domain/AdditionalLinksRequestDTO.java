package com.conx.server.project.domain;

public record AdditionalLinksRequestDTO(
        String linkName,
        String link,
        String explanation
) {

}
