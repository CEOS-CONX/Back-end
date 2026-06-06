package com.conx.server.user.service.browse;

import com.conx.server.user.domain.crew.Crew;
import static com.conx.server.global.common.GetOrDefault.getOrDefault;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewBrowseSort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CrewBrowseService {

    private final UserFinder userFinder;
    private final EvaluationRepository evaluationRepository;
    private final CrewRepository crewRepository;

    @Transactional(readOnly = true)
    public Page<CrewBrowseResponse> getCrews(
            String keyword,
            Industry category,
            CrewType crewType,
            CrewBrowseSort sort,
            int page,
            int size
    ) {
        String normalizedKeyword = normalizeKeyword(keyword);
        CrewBrowseSort browseSort = getOrDefault(sort, CrewBrowseSort.RECENT);
        Pageable pageable = PageRequest.of(page, size);

        return findCrews(
                normalizedKeyword,
                category,
                crewType,
                browseSort,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public CrewBrowseDetailResponse getCrewDetail(Long crewId) {
        Crew crew = userFinder.findActiveCrew(crewId);
        Double point = evaluationRepository.getMeanByCrew(crew).orElse(0.0);

        return CrewBrowseDetailResponse.from(crew, point);
    }

    private Page<CrewBrowseResponse> findCrews(
            String keyword,
            Industry category,
            CrewType crewType,
            CrewBrowseSort sort,
            Pageable pageable
    ) {
        if (sort == CrewBrowseSort.POPULAR) {
            return crewRepository.findBrowseCrewsOrderByPopular(
                    keyword,
                    category,
                    crewType,
                    pageable
            );
        }

        if (sort == CrewBrowseSort.RATING) {
            return crewRepository.findBrowseCrewsOrderByRating(
                    keyword,
                    category,
                    crewType,
                    pageable
            );
        }

        if (sort == CrewBrowseSort.RECOMMENDED) {
            return crewRepository.findBrowseCrewsOrderByRecommended(
                    keyword,
                    category,
                    crewType,
                    pageable
            );
        }

        return crewRepository.findBrowseCrewsOrderByRecent(
                keyword,
                category,
                crewType,
                pageable
        );
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}