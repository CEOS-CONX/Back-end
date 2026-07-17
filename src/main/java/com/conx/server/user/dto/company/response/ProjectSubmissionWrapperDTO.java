package com.conx.server.user.dto.company.response;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.project.domain.AdditionalLinksWrapper;
import com.conx.server.project.domain.ProjectSubmission;

import java.time.LocalDate;
import java.util.List;

public record ProjectSubmissionWrapperDTO(
        String subject,
        LocalDate uploadDate,
        String content,

        List<FileResponseDTO> files,
        List<AdditionalLinksWrapper> additionalLinks
) {
    public static ProjectSubmissionWrapperDTO from(ProjectSubmission submission,
                                                   List<FileResponseDTO> files,
                                                   List<AdditionalLinksWrapper> additionalLinks){
        return new ProjectSubmissionWrapperDTO(submission.getSubject(), submission.getCreatedAt().toLocalDate(),
                submission.getContent(),files, additionalLinks);
    }
}
