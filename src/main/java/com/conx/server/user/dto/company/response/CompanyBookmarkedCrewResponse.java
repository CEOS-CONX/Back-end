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
        Double point
) {

    /*
     * JPQL의 avg() 결과는 Double 타입입니다.
     * 혹시 null이 전달되더라도 0점으로 정규화합니다.
     */
    public CompanyBookmarkedCrewResponse {
        point = point == null ? 0.0 : point;
    }

    public static CompanyBookmarkedCrewResponse of(
            Crew crew,
            double point
    ) {
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