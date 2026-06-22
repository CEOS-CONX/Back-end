package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CompanyPartnerCrewResponse(
        Long projectId,
        String projectName,
        ProjectStatus projectStatus,
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
        String kakaotalkLink
) {

    public static CompanyPartnerCrewResponse of(Project project, Crew crew) {
        return new CompanyPartnerCrewResponse(
                project.getId(),
                project.getName(),
                project.getStatus(),
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
                crew.getKakaotalkLink()
        );
    }
}