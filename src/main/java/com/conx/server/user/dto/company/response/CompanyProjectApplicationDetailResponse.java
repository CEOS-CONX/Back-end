package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CompanyProjectApplicationDetailResponse(
        Long applicationId,
        Long crewId,
        String crewName,
        String profileImage,
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
        String introduction,
        String proposal,
        ProjectApplicationStatus status
) {

    public static CompanyProjectApplicationDetailResponse from(ProjectApplication application) {
        Crew crew = application.getCrew();

        return new CompanyProjectApplicationDetailResponse(
                application.getId(),
                crew.getId(),
                crew.getCrewName(),
                crew.getProfileImage(),
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
                application.getIntroduction(),
                application.getProposal(),
                application.getStatus()
        );
    }
}