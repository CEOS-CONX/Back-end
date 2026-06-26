package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Portfolio;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.repository.PortfolioRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMyPageService {

    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserFinder userFinder;
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public CrewProfileResponse getProfile(Long crewId) {
        Crew crew = userFinder.findActiveCrew(crewId);
        return CrewProfileResponse.from(crew);
    }

    @Transactional
    public CrewProfileResponse updateProfile(Long crewId, CrewProfileUpdateRequest request) {
        Crew crew = userFinder.findActiveCrew(crewId);

        crew.modifyMyPageProfile(
                getOrDefault(request.profileImage(), crew.getProfileImage()),
                getOrDefault(request.crewName(), crew.getCrewName()),
                getOrDefault(request.crewType(), crew.getCrewType()),
                getOrDefault(request.customCrewType(), crew.getCustomCrewType()),
                getOrDefault(request.managerName(), crew.getManagerName()),
                getOrDefault(request.job(), crew.getJob()),
                getOrDefault(request.crewSchool(), crew.getCrewSchool()),
                getOrDefault(request.memberAmount(), crew.getMemberAmount()),
                getOrDefault(request.crewIntroduction(), crew.getCrewIntroduction()),
                getOrDefault(request.additionalIntroduction(), crew.getAdditionalIntroduction()),
                getOrDefault(request.advantages(), crew.getAdvantages()),
                getOrDefault(request.interestingIndustry(), crew.getInterestingIndustry()),
                getOrDefault(request.snsLink(), crew.getSnsLink()),
                getOrDefault(request.etcLink(), crew.getEtcLink()),
                getOrDefault(request.kakaotalkLink(), crew.getKakaotalkLink())
        );

        return CrewProfileResponse.from(crew);
    }

    @Transactional(readOnly = true)
    public Page<CrewBookmarkedProjectResponse> getBookmarkedProjects(Long crewId, Pageable pageable) {
        Crew crew = userFinder.findActiveCrew(crewId);

        return projectBookmarkRepository.findAllByCrewId(crew.getId(), pageable)
                .map(CrewBookmarkedProjectResponse::from);
    }

    @Transactional
    public CrewPortfolioResponseDTO registerPortfolio(Long crewId, CrewPortfolioRequestDTO req){
        Crew crew = userFinder.findActiveCrew(crewId);

        //TODO: PDF 업로드 후 썸네일 이미지 제작 후 업로드 (PDFBox)
        Portfolio portfolio = Portfolio.create(req, crew);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        return CrewPortfolioResponseDTO.create(savedPortfolio);
    }

    @Transactional
    public CrewPortfolioResponseDTO modifyPortfolio(Long crewId, Long portfolioId, ModifyCrewPortfolioRequestDTO req){
        Crew crew = userFinder.findActiveCrew(crewId);

        Portfolio portfolio = portfolioRepository.findByIdAndCrew(portfolioId, crew).orElseThrow(
                () -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND)
        );

        portfolio.modify(req);
        return CrewPortfolioResponseDTO.create(portfolio);
    }

    @Transactional
    public void deletePortfolio(Long crewId, Long portfolioId){
        Crew crew = userFinder.findActiveCrew(crewId);

        portfolioRepository.deleteByIdAndCrew(portfolioId, crew);
    }
}