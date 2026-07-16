package com.conx.server.user.dto.company.response;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.project.domain.AdditionalLinksWrapper;
import com.conx.server.project.domain.ProjectInspectionFeedback;

import java.util.List;

public record ProjectFeedBackWrapperDTO (
        String content,
        List<FileResponseDTO> files,
        List<AdditionalLinksWrapper> links
){
    public static ProjectFeedBackWrapperDTO from(ProjectInspectionFeedback feedback,
                                                 List<FileResponseDTO> files,
                                                 List<AdditionalLinksWrapper> links){
        return new ProjectFeedBackWrapperDTO(feedback.getContent(), files, links);
    }
}
