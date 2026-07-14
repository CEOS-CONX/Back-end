package com.conx.server.user.service.browse;

import com.conx.server.bookmark.repository.CrewBookmarkRepository;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewLink;
import com.conx.server.user.domain.crew.CrewRepresentativeProject;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.crew.CrewBrowseSort;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.dto.crew.response.CrewFileResponse;
import com.conx.server.user.dto.crew.response.CrewLinkResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioItemResponse;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.repository.CrewFileRepository;
import com.conx.server.user.repository.CrewLinkRepository;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.CrewRepresentativeProjectRepository;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.repository.PortfolioRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Service
@RequiredArgsConstructor
public class CrewBrowseService {

    private static final int MAX_PROJECT_HISTORY_SIZE = 8;

    private static final List<ProjectStatus> CREW_HISTORY_STATUSES =
            List.of(
                    ProjectStatus.PROGRESS,
                    ProjectStatus.WAITING_RESULT,
                    ProjectStatus.INSPECTION,
                    ProjectStatus.ADJUSTING,
                    ProjectStatus.DONE
            );

    private final UserFinder userFinder;
    private final EvaluationRepository evaluationRepository;
    private final CrewRepository crewRepository;
    private final CrewBookmarkRepository crewBookmarkRepository;
    private final CrewLinkRepository crewLinkRepository;
    private final CrewFileRepository crewFileRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProjectRepository projectRepository;
    private final CrewRepresentativeProjectRepository
            crewRepresentativeProjectRepository;

    @Transactional(readOnly = true)
    public Page<CrewBrowseResponse> getCrews(
            String keyword,
            Industry category,
            CrewType crewType,
            CrewBrowseSort sort,
            int page,
            int size,
            CustomUserDetails userDetails
    ) {
        String normalizedKeyword =
                normalizeKeyword(keyword);

        CrewBrowseSort browseSort =
                getOrDefault(
                        sort,
                        CrewBrowseSort.RECENT
                );

        Pageable pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.max(size, 1)
                );

        Page<CrewBrowseResponse> crews =
                findCrews(
                        normalizedKeyword,
                        category,
                        crewType,
                        browseSort,
                        pageable
                );

        return applyBookmarkStatus(
                crews,
                userDetails
        );
    }

    @Transactional(readOnly = true)
    public CrewBrowseDetailResponse getCrewDetail(
            Long crewId,
            CustomUserDetails userDetails
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        double crewPoint =
                getCrewPoint(crew);

        boolean bookmarked =
                isCompany(userDetails)
                        && crewBookmarkRepository
                        .existsByCompanyIdAndCrewId(
                                userDetails.getId(),
                                crewId
                        );

        List<CrewLinkResponse> links =
                getCrewLinks(crew);

        List<CrewFileResponse> files =
                crewFileRepository
                        .findAllByCrewIdOrderByIdAsc(
                                crewId
                        )
                        .stream()
                        .map(CrewFileResponse::from)
                        .toList();

        List<CrewPortfolioItemResponse> portfolios =
                portfolioRepository
                        .findAllByCrewIdOrderByIdDesc(
                                crewId
                        )
                        .stream()
                        .map(CrewPortfolioItemResponse::from)
                        .toList();

        List<CrewProjectHistoryResponse>
                representativeProjects =
                getRepresentativeProjects(
                        crewId
                );

        boolean hasPublicDetail =
                hasPublicDetail(
                        crew,
                        links,
                        files,
                        portfolios
                );

        return CrewBrowseDetailResponse.from(
                crew,
                crewPoint,
                bookmarked,
                hasPublicDetail,
                links,
                files,
                portfolios,
                representativeProjects
        );
    }

    @Transactional(readOnly = true)
    public Page<CrewProjectHistoryResponse>
    getCrewProjects(
            Long crewId,
            int page,
            int size,
            CrewProjectHistorySort sort
    ) {
        userFinder.findActiveCrew(crewId);

        int normalizedPage =
                Math.max(page, 0);

        int normalizedSize =
                Math.min(
                        Math.max(size, 1),
                        MAX_PROJECT_HISTORY_SIZE
                );

        Pageable pageable =
                PageRequest.of(
                        normalizedPage,
                        normalizedSize,
                        createProjectHistorySort(sort)
                );

        Page<Project> projectPage =
                projectRepository.findCrewProjectHistory(
                        crewId,
                        CREW_HISTORY_STATUSES,
                        pageable
                );

        List<CrewProjectHistoryResponse> content =
                convertProjectHistory(
                        projectPage.getContent()
                );

        return new PageImpl<>(
                content,
                projectPage.getPageable(),
                projectPage.getTotalElements()
        );
    }

    /*
     * 최신 프로젝트를 자동으로 선택하지 않고,
     * 크루가 직접 선택한 대표 프로젝트만 가져옵니다.
     */
    private List<CrewProjectHistoryResponse>
    getRepresentativeProjects(
            Long crewId
    ) {
        List<Project> projects =
                crewRepresentativeProjectRepository
                        .findAllByCrewIdOrderByDisplayOrderAsc(
                                crewId
                        )
                        .stream()
                        .map(
                                CrewRepresentativeProject::getProject
                        )
                        .toList();

        return convertProjectHistory(projects);
    }

    private List<CrewProjectHistoryResponse>
    convertProjectHistory(
            List<Project> projects
    ) {
        if (
                projects == null
                        || projects.isEmpty()
        ) {
            return List.of();
        }

        List<Long> projectIds =
                projects.stream()
                        .map(Project::getId)
                        .toList();

        Map<Long, Double> pointByProjectId =
                new HashMap<>();

        List<Evaluation> evaluations =
                evaluationRepository
                        .findAllByProjectIdIn(
                                projectIds
                        );

        for (Evaluation evaluation : evaluations) {
            pointByProjectId.put(
                    evaluation.getProject().getId(),
                    evaluation.getMean()
            );
        }

        return projects.stream()
                .map(project ->
                        CrewProjectHistoryResponse.from(
                                project,
                                pointByProjectId.get(
                                        project.getId()
                                )
                        )
                )
                .toList();
    }

    private double getCrewPoint(
            Crew crew
    ) {
        return evaluationRepository
                .getMeanByCrew(crew)
                .orElse(0.0);
    }

    private List<CrewLinkResponse> getCrewLinks(
            Crew crew
    ) {
        List<CrewLink> savedLinks =
                crewLinkRepository
                        .findAllByCrewIdOrderByIdAsc(
                                crew.getId()
                        );

        if (!savedLinks.isEmpty()) {
            return savedLinks.stream()
                    .map(CrewLinkResponse::from)
                    .toList();
        }

        /*
         * 기존 데이터에 CrewLink가 없으면
         * 이전 단일 링크 필드를 fallback으로 반환합니다.
         */
        List<CrewLinkResponse> legacyLinks =
                new ArrayList<>();

        addLegacyLink(
                legacyLinks,
                "SNS",
                crew.getSnsLink()
        );

        addLegacyLink(
                legacyLinks,
                "기타 링크",
                crew.getEtcLink()
        );

        addLegacyLink(
                legacyLinks,
                "카카오톡",
                crew.getKakaotalkLink()
        );

        return legacyLinks;
    }

    private void addLegacyLink(
            List<CrewLinkResponse> links,
            String name,
            String url
    ) {
        if (!hasText(url)) {
            return;
        }

        links.add(
                CrewLinkResponse.legacy(
                        name,
                        url
                )
        );
    }

    /*
     * 대표 프로젝트만 선택해 놓은 경우에는
     * 상세 프로필을 공개한 것으로 판단하지 않습니다.
     */
    private boolean hasPublicDetail(
            Crew crew,
            List<CrewLinkResponse> links,
            List<CrewFileResponse> files,
            List<CrewPortfolioItemResponse> portfolios
    ) {
        return hasText(crew.getActivityField())
                || !crew.getPublicSchools().isEmpty()
                || hasText(crew.getCatchphrase())
                || hasText(crew.getCrewIntroduction())
                || !crew.getPublicAdvantages().isEmpty()
                || !crew.getPublicSpecialties().isEmpty()
                || hasItems(links)
                || hasItems(files)
                || hasItems(portfolios);
    }

    private Sort createProjectHistorySort(
            CrewProjectHistorySort sort
    ) {
        CrewProjectHistorySort resolvedSort =
                getOrDefault(
                        sort,
                        CrewProjectHistorySort.RECENT
                );

        if (
                resolvedSort
                        == CrewProjectHistorySort.OLDEST
        ) {
            return Sort.by(
                    Sort.Order.asc("createdAt"),
                    Sort.Order.asc("id")
            );
        }

        return Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
        );
    }

    private Page<CrewBrowseResponse>
    applyBookmarkStatus(
            Page<CrewBrowseResponse> crews,
            CustomUserDetails userDetails
    ) {
        if (
                !isCompany(userDetails)
                        || crews.isEmpty()
        ) {
            return crews;
        }

        List<Long> crewIds =
                crews.getContent()
                        .stream()
                        .map(CrewBrowseResponse::crewId)
                        .toList();

        Set<Long> bookmarkedCrewIds =
                new HashSet<>(
                        crewBookmarkRepository
                                .findBookmarkedCrewIdsByCompanyIdAndCrewIds(
                                        userDetails.getId(),
                                        crewIds
                                )
                );

        return crews.map(crew ->
                crew.withBookmarked(
                        bookmarkedCrewIds.contains(
                                crew.crewId()
                        )
                )
        );
    }

    private Page<CrewBrowseResponse> findCrews(
            String keyword,
            Industry category,
            CrewType crewType,
            CrewBrowseSort sort,
            Pageable pageable
    ) {
        if (sort == CrewBrowseSort.POPULAR) {
            return crewRepository
                    .findBrowseCrewsOrderByPopular(
                            keyword,
                            category,
                            crewType,
                            pageable
                    );
        }

        if (sort == CrewBrowseSort.RATING) {
            return crewRepository
                    .findBrowseCrewsOrderByRating(
                            keyword,
                            category,
                            crewType,
                            pageable
                    );
        }

        if (sort == CrewBrowseSort.RECOMMENDED) {
            return crewRepository
                    .findBrowseCrewsOrderByRecommended(
                            keyword,
                            category,
                            crewType,
                            pageable
                    );
        }

        return crewRepository
                .findBrowseCrewsOrderByRecent(
                        keyword,
                        category,
                        crewType,
                        pageable
                );
    }

    private boolean isCompany(
            CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return false;
        }

        return userDetails
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        UserRole.COMPANY
                                .getRole()
                                .equals(
                                        authority.getAuthority()
                                )
                );
    }

    private boolean hasItems(
            List<?> values
    ) {
        return values != null
                && !values.isEmpty();
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }

    private String normalizeKeyword(
            String keyword
    ) {
        if (
                keyword == null
                        || keyword.isBlank()
        ) {
            return null;
        }

        return keyword.trim();
    }
}