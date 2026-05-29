package com.conx.server.user.service.browse;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.crew.CrewBrowseSort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.repository.CrewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewBrowseService {

    private final CrewRepository crewRepository;

    @Transactional(readOnly = true)
    public List<CrewBrowseResponse> getCrews(
            String keyword,
            Industry category,
            CrewType crewType,
            CrewBrowseSort sort
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);
        CrewBrowseSort browseSort = getOrDefault(sort, CrewBrowseSort.RECENT);

        return findCrews(
                normalizedKeyword,
                category,
                crewType,
                browseSort
        );
    }

    @Transactional(readOnly = true)
    public CrewBrowseDetailResponse getCrewDetail(Long crewId) {
        Crew crew = crewRepository.findByIdAndStatus(crewId, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        double point = crewRepository.findEvaluationMeanByCrewId(crew.getId())
                .orElse(0.0);

        return CrewBrowseDetailResponse.from(crew, point);
    }

    private List<CrewBrowseResponse> findCrews(
            String keyword,
            Industry category,
            CrewType crewType,
            CrewBrowseSort sort
    ) {
        if (sort == CrewBrowseSort.POPULAR) {
            return crewRepository.findBrowseCrewsOrderByPopular(
                    keyword,
                    category,
                    crewType
            );
        }

        if (sort == CrewBrowseSort.RATING) {
            return crewRepository.findBrowseCrewsOrderByRating(
                    keyword,
                    category,
                    crewType
            );
        }

        if (sort == CrewBrowseSort.RECOMMENDED) {
            return crewRepository.findBrowseCrewsOrderByRecommended(
                    keyword,
                    category,
                    crewType
            );
        }

        return crewRepository.findBrowseCrewsOrderByRecent(
                keyword,
                category,
                crewType
        );
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }

    private <T> T getOrDefault(T newValue, T defaultValue) {
        if (newValue == null) {
            return defaultValue;
        }

        return newValue;
    }
}