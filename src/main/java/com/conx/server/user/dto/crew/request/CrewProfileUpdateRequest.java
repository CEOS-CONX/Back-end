package com.conx.server.user.dto.crew.request;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrewProfileUpdateRequest(
        String profileImage,
        String crewName,
        CrewType crewType,
        String customCrewType,
        String managerName,
        String job,

        String activityField,

        Industry interestingIndustry,

        @PositiveOrZero(
                message = "참여 인원수는 0명 이상이어야 합니다."
        )
        Integer memberAmount,

        @Size(
                max = 30,
                message = "캐치프라이즈는 최대 30자까지 입력할 수 있습니다."
        )
        String catchphrase,

        String crewIntroduction,

        List<String> schools,
        List<String> advantages,
        List<String> specialties,

        @Valid
        List<CrewLinkRequest> links,

        @Valid
        List<CrewFileRequest> files
) {
}