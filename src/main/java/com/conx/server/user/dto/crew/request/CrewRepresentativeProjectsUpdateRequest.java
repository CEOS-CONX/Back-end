package com.conx.server.user.dto.crew.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrewRepresentativeProjectsUpdateRequest(

        @NotNull(
                message = "대표 프로젝트 목록을 입력해주세요."
        )
        @Size(
                max = 3,
                message = "대표 프로젝트는 최대 3개까지 선택할 수 있습니다."
        )
        List<@NotNull Long> projectIds
) {
}