package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;

public record CrewPortfolioRequestDTO(
        String imageLink,
        @NotBlank(message = "포트폴리오 이름을 반드시 입력해주세요") String name,
        @NotBlank(message = "포트폴리오 설명을 반드시 입력해주세요") String description
) {
}
