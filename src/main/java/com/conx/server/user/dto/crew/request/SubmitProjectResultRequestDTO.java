package com.conx.server.user.dto.crew.request;

import java.util.List;

public record SubmitProjectResultRequestDTO(
        List<String> fileLinks,
        String content
) {
}
