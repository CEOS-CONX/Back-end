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
        int cumulative,
        boolean bookmarked
) {

    public CrewBrowseResponse(
            Long crewId,
            String profileImage,
            String crewName,
            String crewIntroduction,
            Industry category,
            CrewType crewType,
            double point,
            int cumulative
    ) {
        this(
                crewId,
                profileImage,
                crewName,
                crewIntroduction,
                category,
                crewType,
                point,
                cumulative,
                false
        );
    }

    public CrewBrowseResponse(
            Long crewId,
            String profileImage,
            String crewName,
            String crewIntroduction,
            Industry category,
            CrewType crewType,
            Double point,
            int cumulative
    ) {
        this(
                crewId,
                profileImage,
                crewName,
                crewIntroduction,
                category,
                crewType,
                point == null ? 0.0 : point,
                cumulative,
                false
        );
    }

    public CrewBrowseResponse withBookmarked(
            boolean bookmarked
    ) {
        return new CrewBrowseResponse(
                crewId,
                profileImage,
                crewName,
                crewIntroduction,
                category,
                crewType,
                point,
                cumulative,
                bookmarked
        );
    }
}