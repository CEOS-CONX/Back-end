package com.conx.server.user.dto.company.response;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

public record CompanyBookmarkedCrewResponse(
        Long crewId,
        String profileImage,
        String crewName,
        String crewIntroduction,
        CrewType crewType,
        String customCrewType,
        Industry interestingIndustry,
        int memberAmount,
        int cumulative,
        double point
) {

    public static CompanyBookmarkedCrewResponse of(Crew crew, double point) {
        return new CompanyBookmarkedCrewResponse(
                crew.getId(),
                crew.getProfileImage(),
                crew.getCrewName(),
                crew.getCrewIntroduction(),
                crew.getCrewType(),
                crew.getCustomCrewType(),
                crew.getInterestingIndustry(),
                crew.getMemberAmount(),
                crew.getTotalSubsidy(),
                point
        );
    }
}