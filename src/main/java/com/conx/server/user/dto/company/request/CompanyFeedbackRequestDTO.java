package com.conx.server.user.dto.company.request;

import com.conx.server.domain.file.dto.FileRequestDTO;
import com.conx.server.project.domain.AdditionalLinksRequestDTO;

import java.util.List;

public record CompanyFeedbackRequestDTO(
        String content,
        List<FileRequestDTO> files,
        List<AdditionalLinksRequestDTO> links
) {
}
