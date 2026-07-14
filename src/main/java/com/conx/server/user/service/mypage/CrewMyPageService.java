package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewFile;
import com.conx.server.user.domain.crew.CrewLink;
import com.conx.server.user.domain.crew.CrewRepresentativeProject;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.domain.crew.Portfolio;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.request.CrewFileRequest;
import com.conx.server.user.dto.crew.request.CrewLinkRequest;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewRepresentativeProjectsUpdateRequest;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewFileResponse;
import com.conx.server.user.dto.crew.response.CrewLinkResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioItemResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.dto.crew.response.CrewRepresentativeProjectCandidateResponse;
import com.conx.server.user.repository.CrewFileRepository;
import com.conx.server.user.repository.CrewLinkRepository;
import com.conx.server.user.repository.CrewRepresentativeProjectRepository;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.repository.PortfolioRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMyPageService {

    private static final long MAX_CREW_FILE_SIZE =
            52_428_800L;

    private static final int MAX_REPRESENTATIVE_PROJECT_SIZE =
            3;

    private static final int MAX_CANDIDATE_PAGE_SIZE =
            20;

    private static final List<ProjectStatus>
            REPRESENTATIVE_PROJECT_STATUSES =
            List.of(
                    ProjectStatus.PROGRESS,
                    ProjectStatus.WAITING_RESULT,
                    ProjectStatus.INSPECTION,
                    ProjectStatus.ADJUSTING,
                    ProjectStatus.DONE
            );

    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserFinder userFinder;
    private final PortfolioRepository portfolioRepository;
    private final CrewLinkRepository crewLinkRepository;
    private final CrewFileRepository crewFileRepository;
    private final ProjectRepository projectRepository;
    private final EvaluationRepository evaluationRepository;
    private final CrewRepresentativeProjectRepository
            crewRepresentativeProjectRepository;

    @Transactional(readOnly = true)
    public CrewProfileResponse getProfile(
            Long crewId
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        return createProfileResponse(crew);
    }

    @Transactional
    public CrewProfileResponse updateProfile(
            Long crewId,
            CrewProfileUpdateRequest request
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        validateCatchphrase(
                request.catchphrase()
        );

        validateFiles(
                request.files()
        );

        crew.modifyMyPageProfile(
                getOrDefault(
                        request.profileImage(),
                        crew.getProfileImage()
                ),
                getOrDefault(
                        request.crewName(),
                        crew.getCrewName()
                ),
                getOrDefault(
                        request.crewType(),
                        crew.getCrewType()
                ),
                getOrDefault(
                        request.customCrewType(),
                        crew.getCustomCrewType()
                ),
                getOrDefault(
                        request.managerName(),
                        crew.getManagerName()
                ),
                getOrDefault(
                        request.job(),
                        crew.getJob()
                ),
                getOrDefault(
                        request.activityField(),
                        crew.getActivityField()
                ),
                getOrDefault(
                        request.memberAmount(),
                        crew.getMemberAmount()
                ),
                getOrDefault(
                        request.catchphrase(),
                        crew.getCatchphrase()
                ),
                getOrDefault(
                        request.crewIntroduction(),
                        crew.getCrewIntroduction()
                ),
                getOrDefault(
                        request.interestingIndustry(),
                        crew.getInterestingIndustry()
                )
        );

        crew.replaceSchools(
                request.schools()
        );

        crew.replaceAdvantages(
                request.advantages()
        );

        crew.replaceSpecialties(
                request.specialties()
        );

        replaceLinks(
                crew,
                request.links()
        );

        replaceFiles(
                crew,
                request.files()
        );

        return createProfileResponse(crew);
    }

    @Transactional(readOnly = true)
    public Page<CrewRepresentativeProjectCandidateResponse>
    getRepresentativeProjectCandidates(
            Long crewId,
            int page,
            int size,
            CrewProjectHistorySort sort
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        Pageable pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(
                                Math.max(size, 1),
                                MAX_CANDIDATE_PAGE_SIZE
                        ),
                        createProjectSort(sort)
                );

        Page<Project> projectPage =
                projectRepository.findCrewProjectHistory(
                        crew.getId(),
                        REPRESENTATIVE_PROJECT_STATUSES,
                        pageable
                );

        Set<Long> selectedProjectIds =
                new HashSet<>(
                        crewRepresentativeProjectRepository
                                .findProjectIdsByCrewId(
                                        crew.getId()
                                )
                );

        return projectPage.map(project ->
                CrewRepresentativeProjectCandidateResponse.from(
                        project,
                        selectedProjectIds.contains(
                                project.getId()
                        )
                )
        );
    }

    @Transactional
    public List<CrewProjectHistoryResponse>
    updateRepresentativeProjects(
            Long crewId,
            CrewRepresentativeProjectsUpdateRequest request
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        List<Long> projectIds =
                request.projectIds();

        validateRepresentativeProjectIds(
                projectIds
        );

        /*
         * 빈 배열이면 대표 프로젝트를 모두 해제합니다.
         */
        if (projectIds.isEmpty()) {
            crewRepresentativeProjectRepository
                    .deleteAllByCrewId(
                            crew.getId()
                    );

            return List.of();
        }

        /*
         * 기존 대표 프로젝트를 삭제하기 전에
         * 요청 프로젝트가 모두 해당 크루의 수행 프로젝트인지 검사합니다.
         */
        List<Project> projects =
                projectRepository
                        .findRepresentativeProjectsForCrew(
                                crew.getId(),
                                projectIds,
                                REPRESENTATIVE_PROJECT_STATUSES
                        );

        if (projects.size() != projectIds.size()) {
            throw new CustomException(
                    ErrorCode.REPRESENTATIVE_PROJECT_NOT_AVAILABLE
            );
        }

        Map<Long, Project> projectById =
                new HashMap<>();

        for (Project project : projects) {
            projectById.put(
                    project.getId(),
                    project
            );
        }

        /*
         * Repository 조회 순서가 아닌
         * 클라이언트가 보낸 projectIds 순서로 다시 정렬합니다.
         */
        List<Project> orderedProjects =
                projectIds.stream()
                        .map(projectById::get)
                        .toList();

        /*
         * 모든 검증이 성공한 후 기존 대표 프로젝트를 삭제합니다.
         */
        crewRepresentativeProjectRepository
                .deleteAllByCrewId(
                        crew.getId()
                );

        List<CrewRepresentativeProject>
                representativeProjects =
                java.util.stream.IntStream
                        .range(
                                0,
                                orderedProjects.size()
                        )
                        .mapToObj(index ->
                                CrewRepresentativeProject.create(
                                        crew,
                                        orderedProjects.get(index),
                                        index
                                )
                        )
                        .toList();

        crewRepresentativeProjectRepository.saveAll(
                representativeProjects
        );

        return convertProjectHistory(
                orderedProjects
        );
    }

    @Transactional(readOnly = true)
    public Page<CrewBookmarkedProjectResponse>
    getBookmarkedProjects(
            Long crewId,
            Pageable pageable
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        return projectBookmarkRepository
                .findAllByCrewId(
                        crew.getId(),
                        pageable
                )
                .map(
                        CrewBookmarkedProjectResponse::from
                );
    }

    @Transactional
    public CrewPortfolioResponseDTO registerPortfolio(
            Long crewId,
            CrewPortfolioRequestDTO request
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        Portfolio portfolio =
                Portfolio.create(
                        crew,
                        request.name(),
                        request.description(),
                        request.fileUrl(),
                        request.imageUrl()
                );

        Portfolio savedPortfolio =
                portfolioRepository.save(portfolio);

        return CrewPortfolioResponseDTO.create(
                savedPortfolio
        );
    }

    @Transactional
    public CrewPortfolioResponseDTO modifyPortfolio(
            Long crewId,
            Long portfolioId,
            ModifyCrewPortfolioRequestDTO request
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        Portfolio portfolio =
                findCrewPortfolio(
                        crew,
                        portfolioId
                );

        portfolio.modify(request);

        return CrewPortfolioResponseDTO.create(
                portfolio
        );
    }

    @Transactional
    public void deletePortfolio(
            Long crewId,
            Long portfolioId
    ) {
        Crew crew =
                userFinder.findActiveCrew(crewId);

        Portfolio portfolio =
                findCrewPortfolio(
                        crew,
                        portfolioId
                );

        portfolioRepository.delete(portfolio);
    }

    private Portfolio findCrewPortfolio(
            Crew crew,
            Long portfolioId
    ) {
        return portfolioRepository
                .findByIdAndCrew(
                        portfolioId,
                        crew
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.PORTFOLIO_NOT_FOUND
                        )
                );
    }

    private CrewProfileResponse createProfileResponse(
            Crew crew
    ) {
        List<CrewLinkResponse> links =
                crewLinkRepository
                        .findAllByCrewIdOrderByIdAsc(
                                crew.getId()
                        )
                        .stream()
                        .map(CrewLinkResponse::from)
                        .toList();

        List<CrewFileResponse> files =
                crewFileRepository
                        .findAllByCrewIdOrderByIdAsc(
                                crew.getId()
                        )
                        .stream()
                        .map(CrewFileResponse::from)
                        .toList();

        List<CrewPortfolioItemResponse> portfolios =
                portfolioRepository
                        .findAllByCrewIdOrderByIdDesc(
                                crew.getId()
                        )
                        .stream()
                        .map(CrewPortfolioItemResponse::from)
                        .toList();

        List<CrewProjectHistoryResponse>
                representativeProjects =
                getRepresentativeProjects(
                        crew.getId()
                );

        return CrewProfileResponse.from(
                crew,
                links,
                files,
                portfolios,
                representativeProjects
        );
    }

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

    private void validateRepresentativeProjectIds(
            List<Long> projectIds
    ) {
        if (projectIds == null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        if (
                projectIds.size()
                        > MAX_REPRESENTATIVE_PROJECT_SIZE
        ) {
            throw new CustomException(
                    ErrorCode.REPRESENTATIVE_PROJECT_LIMIT_EXCEEDED
            );
        }

        if (
                projectIds.stream()
                        .anyMatch(Objects::isNull)
        ) {
            throw new CustomException(
                    ErrorCode.REPRESENTATIVE_PROJECT_NOT_AVAILABLE
            );
        }

        if (
                new HashSet<>(projectIds).size()
                        != projectIds.size()
        ) {
            throw new CustomException(
                    ErrorCode.REPRESENTATIVE_PROJECT_DUPLICATED
            );
        }
    }

    private Sort createProjectSort(
            CrewProjectHistorySort sort
    ) {
        if (
                sort
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

    private void replaceLinks(
            Crew crew,
            List<CrewLinkRequest> requests
    ) {
        if (requests == null) {
            return;
        }

        crewLinkRepository.deleteAllByCrewId(
                crew.getId()
        );

        List<CrewLink> links =
                requests.stream()
                        .filter(request ->
                                request != null
                                        && hasText(
                                        request.url()
                                )
                        )
                        .map(request ->
                                CrewLink.create(
                                        crew,
                                        request.name(),
                                        request.url(),
                                        request.description()
                                )
                        )
                        .toList();

        if (!links.isEmpty()) {
            crewLinkRepository.saveAll(links);
        }
    }

    private void replaceFiles(
            Crew crew,
            List<CrewFileRequest> requests
    ) {
        if (requests == null) {
            return;
        }

        crewFileRepository.deleteAllByCrewId(
                crew.getId()
        );

        List<CrewFile> files =
                requests.stream()
                        .filter(request ->
                                request != null
                                        && hasText(
                                        request.url()
                                )
                        )
                        .map(request ->
                                CrewFile.create(
                                        crew,
                                        request.fileName(),
                                        request.extension(),
                                        request.size(),
                                        request.url(),
                                        request.description()
                                )
                        )
                        .toList();

        if (!files.isEmpty()) {
            crewFileRepository.saveAll(files);
        }
    }

    private void validateCatchphrase(
            String catchphrase
    ) {
        if (
                catchphrase != null
                        && catchphrase.length() > 30
        ) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }
    }

    private void validateFiles(
            List<CrewFileRequest> requests
    ) {
        if (requests == null) {
            return;
        }

        boolean hasInvalidFile =
                requests.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(request ->
                                request.size() == null
                                        || request.size() < 0
                                        || request.size()
                                        > MAX_CREW_FILE_SIZE
                                        || !hasText(
                                        request.fileName()
                                )
                                        || !hasText(
                                        request.url()
                                )
                        );

        if (hasInvalidFile) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }
}