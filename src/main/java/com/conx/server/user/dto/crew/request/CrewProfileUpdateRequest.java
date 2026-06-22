package com.conx.server.user.dto.crew.request;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CrewProfileUpdateRequest(
        String profileImage,
        String crewName,
        CrewType crewType,
        String customCrewType,
        String managerName,
        String job,
        String crewSchool,
        Integer memberAmount,
        String crewIntroduction,
        String additionalIntroduction,
        List<String> advantages,
        Industry interestingIndustry,
        String snsLink,
        String etcLink,
        String kakaotalkLink
) {
}