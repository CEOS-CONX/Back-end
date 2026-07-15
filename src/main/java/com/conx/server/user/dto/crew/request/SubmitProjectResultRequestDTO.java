package com.conx.server.user.dto.crew.request;

import com.conx.server.project.domain.AdditionalLinksWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitProjectResultRequestDTO(
        List<String> fileLinks,
        List<AdditionalLinksWrapper> links,

        String subject,
        String content
) {
}
