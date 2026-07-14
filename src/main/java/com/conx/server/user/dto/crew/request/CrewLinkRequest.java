package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrewLinkRequest(

        @NotBlank(
                message = "링크 이름을 입력해주세요."
        )
        @Size(
                max = 100,
                message = "링크 이름은 최대 100자까지 입력할 수 있습니다."
        )
        String name,

        @NotBlank(
                message = "링크 URL을 입력해주세요."
        )
        @Size(
                max = 2000,
                message = "링크 URL은 최대 2000자까지 입력할 수 있습니다."
        )
        String url,

        @Size(
                max = 500,
                message = "링크 설명은 최대 500자까지 입력할 수 있습니다."
        )
        String description
) {
}