package com.conx.server.user.dto.crew.request;

import com.conx.server.project.domain.AdditionalLinksWrapper;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SubmitProjectResultRequestDTO(

        List<String> fileLinks,

        List<AdditionalLinksWrapper> links,

        @NotBlank(message = "결과물 제목을 입력해주세요")
        String subject,

        String content
) {

    public SubmitProjectResultRequestDTO {
        fileLinks =
                fileLinks == null
                        ? List.of()
                        : List.copyOf(fileLinks);

        links =
                links == null
                        ? List.of()
                        : List.copyOf(links);

        subject =
                subject == null
                        ? null
                        : subject.trim();
    }


    public SubmitProjectResultRequestDTO(
            List<String> fileLinks,
            String content
    ) {
        this(
                fileLinks,
                List.of(),
                "결과물 제출",
                content
        );
    }
}