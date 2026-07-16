package com.conx.server.user.dto.crew.response;

import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;

public record CrewBrowseResponse(
        Long crewId,
        String profileImage,
        String crewName,
        String crewIntroduction,
        Industry category,
        CrewType crewType,
        double point,
        int cumulative,
        boolean bookmarked
) {

    /*
     * Repository의 JPQL 생성자 조회에서 사용합니다.
     * 목록 조회 직후 북마크 여부를 별도로 반영하므로 기본값은 false입니다.
     */
    public CrewBrowseResponse(
            Long crewId,
            String profileImage,
            String crewName,
            String crewIntroduction,
            Industry category,
            CrewType crewType,
            double point,
            int cumulative
    ) {
        this(
                crewId,
                profileImage,
                crewName,
                crewIntroduction,
                category,
                crewType,
                point,
                cumulative,
                false
        );
    }

    /*
     * JPQL의 평균값이 Double 또는 null로 전달되는 경우를 처리합니다.
     */
    public CrewBrowseResponse(
            Long crewId,
            String profileImage,
            String crewName,
            String crewIntroduction,
            Industry category,
            CrewType crewType,
            Double point,
            int cumulative
    ) {
        this(
                crewId,
                profileImage,
                crewName,
                crewIntroduction,
                category,
                crewType,
                point == null ? 0.0 : point,
                cumulative,
                false
        );
    }

    public CrewBrowseResponse withBookmarked(
            boolean bookmarked
    ) {
        return new CrewBrowseResponse(
                crewId,
                profileImage,
                crewName,
                crewIntroduction,
                category,
                crewType,
                point,
                cumulative,
                bookmarked
        );
    }
}