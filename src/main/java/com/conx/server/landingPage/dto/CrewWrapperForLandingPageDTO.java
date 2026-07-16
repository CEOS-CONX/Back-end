package com.conx.server.landingPage.dto;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

public record CrewWrapperForLandingPageDTO(
        long crewId,
        String crewImageLink,
        String crewName,
        String crewIntroduction,
        Industry industry,
        CrewType crewType,
        double point,
        int totalProject
) {

    /**
     * Evaluation이 없는 크루의 평점이 null로 조회되는 경우
     * 0.0으로 변환하기 위한 생성자
     */
    public CrewWrapperForLandingPageDTO(
            long crewId,
            String crewImageLink,
            String crewName,
            String crewIntroduction,
            Industry industry,
            CrewType crewType,
            Double point,
            int totalProject
    ) {
        this(
                crewId,
                crewImageLink,
                crewName,
                crewIntroduction,
                industry,
                crewType,
                point == null
                        ? 0.0
                        : point,
                totalProject
        );
    }

    /**
     * feature/2의 기존 cumulative() 호출부 호환용
     */
    public int cumulative() {
        return totalProject;
    }
}