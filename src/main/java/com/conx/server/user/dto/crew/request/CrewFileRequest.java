package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CrewFileRequest(

        @NotBlank(
                message = "파일명을 입력해주세요."
        )
        @Size(
                max = 255,
                message = "파일명은 최대 255자까지 입력할 수 있습니다."
        )
        String fileName,

        @Size(
                max = 20,
                message = "파일 확장자는 최대 20자까지 입력할 수 있습니다."
        )
        String extension,

        @NotNull(
                message = "파일 크기를 입력해주세요."
        )
        @PositiveOrZero(
                message = "파일 크기는 0 이상이어야 합니다."
        )
        @Max(
                value = 52_428_800L,
                message = "크루 소개 파일은 최대 50MB까지 등록할 수 있습니다."
        )
        Long size,

        @NotBlank(
                message = "파일 URL을 입력해주세요."
        )
        @Size(
                max = 2000,
                message = "파일 URL은 최대 2000자까지 입력할 수 있습니다."
        )
        String url,

        @Size(
                max = 500,
                message = "파일 설명은 최대 500자까지 입력할 수 있습니다."
        )
        String description
) {
}