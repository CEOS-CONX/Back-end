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

        String activityField,
        Industry interestingIndustry,

        List<String> schools,
        int memberAmount,

        String catchphrase,
        String crewIntroduction,

        List<String> advantages,
        List<String> specialties,

        List<CrewLinkResponse> links,
        List<CrewFileResponse> files,
        List<CrewPortfolioItemResponse> portfolios,
        List<CrewProjectHistoryResponse> representativeProjects,

        int totalSubsidy
) {

    public static CrewProfileResponse from(
            Crew crew,
            List<CrewLinkResponse> links,
            List<CrewFileResponse> files,
            List<CrewPortfolioItemResponse> portfolios,
            List<CrewProjectHistoryResponse> representativeProjects
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

                crew.getActivityField(),
                crew.getInterestingIndustry(),

                crew.getPublicSchools(),
                crew.getMemberAmount(),

                crew.getCatchphrase(),
                crew.getCrewIntroduction(),

                crew.getPublicAdvantages(),
                crew.getPublicSpecialties(),

                safeList(links),
                safeList(files),
                safeList(portfolios),
                safeList(representativeProjects),

                crew.getTotalSubsidy()
        );
    }

    private static <T> List<T> safeList(
            List<T> values
    ) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(value -> value != null)
                .toList();
    }
}