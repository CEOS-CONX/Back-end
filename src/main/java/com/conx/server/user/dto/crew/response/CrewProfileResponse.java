package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CrewProfileResponse(
        Long crewId,
        String email,
        String profileImage,
        String crewName,
        CrewType crewType,
        String customCrewType,
        String managerName,
        String managerPhoneNumber,
        String job,

        String crewSchool,
        int memberAmount,

        String crewIntroduction,
        String additionalIntroduction,

        List<String> advantages,
        Industry interestingIndustry,

        String snsLink,
        String etcLink,
        String kakaotalkLink,

        int totalSubsidy,

        List<String> schools,
        List<String> specialties,
        List<CrewLinkResponse> links,
        List<CrewFileResponse> files
) {

    public static CrewProfileResponse from(
            Crew crew,
            List<CrewLinkResponse> links,
            List<CrewFileResponse> files
    ) {
        return new CrewProfileResponse(
                crew.getId(),
                crew.getEmail(),
                crew.getProfileImage(),
                crew.getCrewName(),
                crew.getCrewType(),
                crew.getCustomCrewType(),
                crew.getManagerName(),
                crew.getManagerPhoneNumber(),
                crew.getJob(),

                crew.getCrewSchool(),
                crew.getMemberAmount(),

                crew.getCrewIntroduction(),
                crew.getAdditionalIntroduction(),

                crew.getAdvantages(),
                crew.getInterestingIndustry(),

                crew.getSnsLink(),
                crew.getEtcLink(),
                crew.getKakaotalkLink(),

                crew.getTotalSubsidy(),

                crew.getPublicSchools(),
                crew.getPublicSpecialties(),
                links == null ? List.of() : List.copyOf(links),
                files == null ? List.of() : List.copyOf(files)
        );
    }

    public static CrewProfileResponse from(Crew crew) {
        return from(
                crew,
                List.of(),
                List.of()
        );
    }
}