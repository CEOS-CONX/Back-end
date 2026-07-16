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
     * 평가 정보가 없는 경우 0점으로 반환합니다.
     */
    public CompanyBookmarkedCrewResponse {
        point = point == null ? 0.0 : point;
    }

    /*
     * 별점까지 함께 조회한 경우 사용합니다.
     */
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

    /*
     * 기존 코드가 from(Crew)를 사용하는 경우를 위한 호환 메서드입니다.
     */
    public static CompanyBookmarkedCrewResponse from(
            Crew crew
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
                0.0
        );
    }
}