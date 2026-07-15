package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SubmitProjectResultRequestDTO(

        @NotBlank(
                message = "결과물 제목을 입력해주세요"
        )
        String title,

        List<String> fileLinks,

        List<String> referenceLinks,

        String content
) {

        public SubmitProjectResultRequestDTO {
                title =
                        title == null
                                ? null
                                : title.trim();

                fileLinks =
                        fileLinks == null
                                ? List.of()
                                : List.copyOf(fileLinks);

                referenceLinks =
                        referenceLinks == null
                                ? List.of()
                                : List.copyOf(referenceLinks);
        }

        /**
         * 기존 테스트 및 기존 코드 호환용 생성자
         */
        public SubmitProjectResultRequestDTO(
                List<String> fileLinks,
                String content
        ) {
                this(
                        "결과물 제출",
                        fileLinks,
                        List.of(),
                        content
                );
        }
}