package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

public record CrewBrowseResponse(
        Long crewId,
        String profileImage,
        String crewName,
        String crewIntroduction,
        Industry category,
        CrewType crewType,
        double point,
        int cumulative
) {
}