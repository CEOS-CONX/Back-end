package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.domain.CrewBookmark;
import com.conx.server.bookmark.repository.CrewBookmarkRepository;
import static com.conx.server.global.common.GetOrDefault.getOrDefault;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.company.request.CompanyAccountUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyProfileUpdateRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import com.conx.server.user.dto.company.response.CompanyCrewBookmarkToggleResponse;
import com.conx.server.user.dto.company.response.CompanyProfileResponse;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyMyPageService {

    private final UserFinder userFinder;
    private final CrewBookmarkRepository crewBookmarkRepository;

    @Transactional(readOnly = true)
    public CompanyProfileResponse getProfile(Long companyId) {
        Company company = userFinder.findActiveCompany(companyId);
        return CompanyProfileResponse.from(company);
    }

    @Transactional
    public CompanyProfileResponse updateProfile(Long companyId, CompanyProfileUpdateRequest request) {
        Company company = userFinder.findActiveCompany(companyId);


        company.modifyProfile(
                getOrDefault(request.companyName(), company.getCompanyName()),
                getOrDefault(request.brandName(), company.getBrandName()),
                getOrDefault(request.industry(), company.getIndustry()),
                getOrDefault(request.customIndustry(), company.getCustomIndustry()),
                getOrDefault(request.companyIntroduction(), company.getCompanyIntroduction()),
                getOrDefault(request.homepageLink(), company.getHomepageLink()),
                getOrDefault(request.additionalFileLink(), company.getAdditionalFileLink()),
                getOrDefault(request.profileImage(), company.getProfileImage())
        );

        return CompanyProfileResponse.from(company);
    }

    @Transactional(readOnly = true)
    public CompanyAccountResponse getAccount(Long companyId) {
        Company company = userFinder.findActiveCompany(companyId);
        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyAccountResponse updateAccount(Long companyId, CompanyAccountUpdateRequest request) {
        Company company = userFinder.findActiveCompany(companyId);

        company.modifyAccount(
                getOrDefault(request.companyName(), company.getCompanyName()),
                getOrDefault(request.businessRegistrationNumber(), company.getBusinessRegistrationNumber()),
                getOrDefault(request.managerName(), company.getManagerName()),
                getOrDefault(request.job(), company.getJob())
        );

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyCrewBookmarkToggleResponse toggleCrewBookmark(Long companyId, Long crewId) {
        Company company = userFinder.findActiveCompany(companyId);
        Crew crew = userFinder.findActiveCrew(crewId);

        return crewBookmarkRepository.findByCompanyIdAndCrewId(company.getId(), crew.getId())
                .map(crewBookmark -> {
                    crewBookmarkRepository.delete(crewBookmark);
                    return CompanyCrewBookmarkToggleResponse.of(crew.getId(), false);
                })
                .orElseGet(() -> {
                    CrewBookmark crewBookmark = CrewBookmark.create(company, crew);
                    crewBookmarkRepository.save(crewBookmark);
                    return CompanyCrewBookmarkToggleResponse.of(crew.getId(), true);
                });
    }

    @Transactional(readOnly = true)
    public List<CompanyBookmarkedCrewResponse> getBookmarkedCrews(Long companyId) {
        Company company = userFinder.findActiveCompany(companyId);

        return crewBookmarkRepository.findAllByCompanyId(company.getId())
                .stream()
                .map(CrewBookmark::getCrew)
                .map(CompanyBookmarkedCrewResponse::from)
                .toList();
    }
}