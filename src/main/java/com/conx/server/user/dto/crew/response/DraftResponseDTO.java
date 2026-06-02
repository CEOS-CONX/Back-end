package com.conx.server.user.dto.crew.response;

import java.util.List;

public record DraftResponseDTO(
        List<String> fileLinks,
        String content
) {
}
