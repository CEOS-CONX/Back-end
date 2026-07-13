package com.conx.server.landingPage.dto;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

public record CrewWrapperForLandingPageDTO(
        long crewId,
        String crewImageLink,
        String crewName,
        String crewIntroduction,

        Industry industry,
        CrewType crewType,
        double point,
        int totalProject
) {}
