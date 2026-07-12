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

        List<String> schools,
        Integer memberAmount,
        Industry category,

        String crewIntroduction,
        List<String> advantages,
        List<String> specialties,

        List<CrewLinkResponse> links,
        List<CrewFileResponse> files,
        List<CrewPortfolioItemResponse> portfolios,
        List<CrewProjectHistoryResponse> representativeProjects,

        boolean hasPublicDetail,
        boolean bookmarked,

        double point,
        int cumulative
) {

    public static CrewBrowseDetailResponse from(
            Crew crew,
            double point,
            boolean bookmarked,
            boolean hasPublicDetail,
            String crewIntroduction,
            List<CrewLinkResponse> links,
            List<CrewFileResponse> files,
            List<CrewPortfolioItemResponse> portfolios,
            List<CrewProjectHistoryResponse> representativeProjects
    ) {
        /*
         * 상세 정보를 공개하지 않은 크루는
         * hasPublicDetail만 확인해도 프론트에서 빈 화면을 처리할 수 있도록
         * 상세 입력 항목을 null로 반환합니다.
         */
        if (!hasPublicDetail) {
            return new CrewBrowseDetailResponse(
                    crew.getId(),
                    crew.getProfileImage(),
                    crew.getCrewName(),
                    crew.getCrewType(),
                    crew.getCustomCrewType(),

                    null,
                    null,
                    crew.getInterestingIndustry(),

                    null,
                    null,
                    null,

                    null,
                    null,
                    null,
                    null,

                    false,
                    bookmarked,

                    point,
                    crew.getTotalSubsidy()
            );
        }

        return new CrewBrowseDetailResponse(
                crew.getId(),
                crew.getProfileImage(),
                crew.getCrewName(),
                crew.getCrewType(),
                crew.getCustomCrewType(),

                crew.getPublicSchools(),
                crew.getMemberAmount(),
                crew.getInterestingIndustry(),

                crewIntroduction,
                safeList(crew.getAdvantages()),
                crew.getPublicSpecialties(),

                safeList(links),
                safeList(files),
                safeList(portfolios),
                safeList(representativeProjects),

                true,
                bookmarked,

                point,
                crew.getTotalSubsidy()
        );
    }

    private static <T> List<T> safeList(List<T> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(value -> value != null)
                .toList();
    }
}