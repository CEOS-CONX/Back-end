package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewFile;
import com.conx.server.user.domain.crew.CrewLink;
import com.conx.server.user.domain.crew.Portfolio;
import com.conx.server.user.dto.crew.request.CrewFileRequest;
import com.conx.server.user.dto.crew.request.CrewLinkRequest;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewFileResponse;
import com.conx.server.user.dto.crew.response.CrewLinkResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.repository.CrewFileRepository;
import com.conx.server.user.repository.CrewLinkRepository;
import com.conx.server.user.repository.PortfolioRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMyPageService {

    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserFinder userFinder;
    private final PortfolioRepository portfolioRepository;
    private final CrewLinkRepository crewLinkRepository;
    private final CrewFileRepository crewFileRepository;

    @Transactional(readOnly = true)
    public CrewProfileResponse getProfile(Long crewId) {
        Crew crew = userFinder.findActiveCrew(crewId);

        return createProfileResponse(crew);
    }

    @Transactional
    public CrewProfileResponse updateProfile(
            Long crewId,
            CrewProfileUpdateRequest request
    ) {
        Crew crew = userFinder.findActiveCrew(crewId);

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
                        request.crewSchool(),
                        crew.getCrewSchool()
                ),
                getOrDefault(
                        request.memberAmount(),
                        crew.getMemberAmount()
                ),
                getOrDefault(
                        request.crewIntroduction(),
                        crew.getCrewIntroduction()
                ),
                getOrDefault(
                        request.additionalIntroduction(),
                        crew.getAdditionalIntroduction()
                ),
                getOrDefault(
                        request.advantages(),
                        crew.getAdvantages()
                ),
                getOrDefault(
                        request.interestingIndustry(),
                        crew.getInterestingIndustry()
                ),
                getOrDefault(
                        request.snsLink(),
                        crew.getSnsLink()
                ),
                getOrDefault(
                        request.etcLink(),
                        crew.getEtcLink()
                ),
                getOrDefault(
                        request.kakaotalkLink(),
                        crew.getKakaotalkLink()
                )
        );

        crew.replaceSchools(request.schools());
        crew.replaceSpecialties(request.specialties());

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
    public Page<CrewBookmarkedProjectResponse>
    getBookmarkedProjects(
            Long crewId,
            Pageable pageable
    ) {
        Crew crew = userFinder.findActiveCrew(crewId);

        return projectBookmarkRepository
                .findAllByCrewId(
                        crew.getId(),
                        pageable
                )
                .map(CrewBookmarkedProjectResponse::from);
    }

    @Transactional
    public CrewPortfolioResponseDTO registerPortfolio(
            Long crewId,
            CrewPortfolioRequestDTO request
    ) {
        userFinder.findActiveCrew(crewId);

        /*
         * PDF 썸네일 생성 로직이 아직 없으므로
         * 기존 등록 기능은 현재 상태를 유지합니다.
         */
        return null;
    }

    @Transactional
    public CrewPortfolioResponseDTO modifyPortfolio(
            Long crewId,
            Long portfolioId,
            ModifyCrewPortfolioRequestDTO request
    ) {
        Crew crew = userFinder.findActiveCrew(crewId);

        Portfolio portfolio = portfolioRepository
                .findByIdAndCrew(
                        portfolioId,
                        crew
                )
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.PORTFOLIO_NOT_FOUND
                        )
                );

        portfolio.modify(request);

        return CrewPortfolioResponseDTO.create(portfolio);
    }

    @Transactional
    public void deletePortfolio(
            Long crewId,
            Long portfolioId
    ) {
        Crew crew = userFinder.findActiveCrew(crewId);

        portfolioRepository.deleteByIdAndCrew(
                portfolioId,
                crew
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

        return CrewProfileResponse.from(
                crew,
                links,
                files
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

        List<CrewLink> links = requests.stream()
                .filter(request ->
                        request != null
                                && hasText(request.url())
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

        List<CrewFile> files = requests.stream()
                .filter(request ->
                        request != null
                                && hasText(request.url())
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}