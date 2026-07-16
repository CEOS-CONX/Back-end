package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CrewBrowseDetailResponse(
        Long crewId,
        String profileImage,
        String crewName,
        CrewType crewType,
        String customCrewType,
        String crewSchool,
        int memberAmount,
        String crewIntroduction,
        String additionalIntroduction,
        List<String> advantages,
        Industry interestingIndustry,
        String snsLink,
        String etcLink,
        String kakaotalkLink,
        double point,
        int totalProject
) {

    public static CrewBrowseDetailResponse from(Crew crew, double point) {
        return new CrewBrowseDetailResponse(
                crew.getId(),
                crew.getProfileImage(),
                crew.getCrewName(),
                crew.getCrewType(),
                crew.getCustomCrewType(),
                crew.getCrewSchool(),
                crew.getMemberAmount(),
                crew.getCrewIntroduction(),
                crew.getAdditionalIntroduction(),
                crew.getAdvantages(),
                crew.getInterestingIndustry(),
                crew.getSnsLink(),
                crew.getEtcLink(),
                crew.getKakaotalkLink(),
                point,
                crew.getTotalSubsidy()
        );
    }
}