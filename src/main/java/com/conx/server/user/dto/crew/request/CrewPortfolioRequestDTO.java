package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrewPortfolioRequestDTO(

        @Size(
                max = 2000,
                message = "썸네일 이미지 URL은 최대 2000자까지 입력할 수 있습니다."
        )
        String imageUrl,

        @NotBlank(
                message = "포트폴리오 이름을 반드시 입력해주세요."
        )
        @Size(
                max = 100,
                message = "포트폴리오 이름은 최대 100자까지 입력할 수 있습니다."
        )
        String name,

        @NotBlank(
                message = "포트폴리오 설명을 반드시 입력해주세요."
        )
        @Size(
                max = 1000,
                message = "포트폴리오 설명은 최대 1000자까지 입력할 수 있습니다."
        )
        String description,

        @NotBlank(
                message = "포트폴리오 파일 URL을 반드시 입력해주세요."
        )
        @Size(
                max = 2000,
                message = "포트폴리오 파일 URL은 최대 2000자까지 입력할 수 있습니다."
        )
        String fileUrl
) {
}