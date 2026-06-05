package com.conx.server.landingPage.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.landingPage.dto.AnonymousLandingPageResponseDTO;
import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.landingPage.service.AnonymousLandingPageService;
import com.conx.server.landingPage.service.CompanyLandingPageService;
import com.conx.server.landingPage.service.CrewLandingPageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/landing")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LandingPageController {

    private final CompanyLandingPageService companyLandingService;
    private final CrewLandingPageService crewLandingPage;
    private final AnonymousLandingPageService anonymousLandingPageService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 비로그인 사용자의 랜딩페이지입니다.
     */
    @GetMapping
    public ApiResponse<AnonymousLandingPageResponseDTO> landingAnonymous(){
        AnonymousLandingPageResponseDTO response = anonymousLandingPageService.landing();
        return apiResponseFactory.success(response, null);
    }

    /**
     * 기업 사용자의 랜딩페이지입니다.
     * @param userDetails 인증정보
     */
    @GetMapping("/company")
    public ApiResponse<List<CrewWrapperForLandingPageDTO>> landingCompany(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        List<CrewWrapperForLandingPageDTO> crew = companyLandingService.landing();
        return apiResponseFactory.success(crew, userDetails);
    }

    /**
     * 크루 사용자의 랜딩페이지입니다.
     * @param userDetails 인증정보
     */
    @GetMapping("/crew")
    public ApiResponse<List<ProjectWrapperForLandingPageDTO>> landingCrew(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<ProjectWrapperForLandingPageDTO> project = crewLandingPage.landing();
        return apiResponseFactory.success(project, userDetails);
    }
}