package com.conx.server.user.dto.crew.request;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

import java.util.List;

public record CrewProfileUpdateRequest(
        String profileImage,
        String crewName,
        CrewType crewType,
        String customCrewType,
        String managerName,
        String job,
        String crewSchool,
        Integer memberAmount,
        String crewIntroduction,
        String additionalIntroduction,
        List<String> advantages,
        Industry interestingIndustry,
        String snsLink,
        String etcLink,
        String kakaotalkLink,

        List<String> schools,
        List<String> specialties,
        List<CrewLinkRequest> links,
        List<CrewFileRequest> files
) {

    /*
     * 기존 코드와 테스트의 생성자 호출을 유지하기 위한 생성자입니다.
     */
    public CrewProfileUpdateRequest(
            String profileImage,
            String crewName,
            CrewType crewType,
            String customCrewType,
            String managerName,
            String job,
            String crewSchool,
            Integer memberAmount,
            String crewIntroduction,
            String additionalIntroduction,
            List<String> advantages,
            Industry interestingIndustry,
            String snsLink,
            String etcLink,
            String kakaotalkLink
    ) {
        this(
                profileImage,
                crewName,
                crewType,
                customCrewType,
                managerName,
                job,
                crewSchool,
                memberAmount,
                crewIntroduction,
                additionalIntroduction,
                advantages,
                interestingIndustry,
                snsLink,
                etcLink,
                kakaotalkLink,
                null,
                null,
                null,
                null
        );
    }
}