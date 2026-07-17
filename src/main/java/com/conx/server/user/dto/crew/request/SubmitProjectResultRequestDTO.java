package com.conx.server.user.dto.crew.request;

import com.conx.server.project.domain.AdditionalLinksWrapper;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SubmitProjectResultRequestDTO(

        List<String> fileLinks,

        List<AdditionalLinksWrapper> links,

        @NotBlank(
                message = "결과물 제목을 입력해주세요"
        )
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

    /**
     * feature/2의 기존 title() 호출부 호환용
     */
    public String title() {
        return subject;
    }

    /**
     * feature/2의 기존 referenceLinks() 호출부 호환용
     */
    public List<AdditionalLinksWrapper> referenceLinks() {
        return links;
    }

    /**
     * 기존 테스트 및 코드 호환용 생성자
     */
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