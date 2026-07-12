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
        int cumulative
) {

    public CrewWrapperForLandingPageDTO(
            long crewId,
            String crewImageLink,
            String crewName,
            String crewIntroduction,
            Industry industry,
            CrewType crewType,
            Double point,
            int cumulative
    ) {
        this(
                crewId,
                crewImageLink,
                crewName,
                crewIntroduction,
                industry,
                crewType,
                point == null ? 0.0 : point,
                cumulative
        );
    }
}