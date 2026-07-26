package com.conx.server.project.dto.response;

import com.conx.server.project.domain.AdditionalLinksWrapper;

public record AdditionalLinkResponse(
        String linkName,
        String link,
        String explanation
) {

    public static AdditionalLinkResponse from(
            AdditionalLinksWrapper additionalLink
    ) {
        return new AdditionalLinkResponse(
                additionalLink.getLinkName(),
                additionalLink.getLink(),
                additionalLink.getExplanation()
        );
    }
}
