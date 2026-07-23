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
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "비로그인 랜딩 페이지 조회",
            description = "비로그인 사용자에게 모집 중인 프로젝트와 활성 크루 목록을 반환합니다. 프로젝트는 조회수순, 크루는 평가 평균과 누적 프로젝트 수를 기준으로 정렬되며 로그인 사용자는 호출할 수 없습니다."
    )
    @GetMapping
    public ApiResponse<AnonymousLandingPageResponseDTO> landingAnonymous(){
        AnonymousLandingPageResponseDTO response = anonymousLandingPageService.landing();
        return apiResponseFactory.success(response, null);
    }

    /**
     * 기업 사용자의 랜딩페이지입니다.
     * @param userDetails 인증정보
     */
    @Operation(
            summary = "기업 랜딩 크루 목록 조회",
            description = "COMPANY 사용자에게 활성 크루 목록을 평가 평균과 누적 프로젝트 수 기준으로 정렬하여 반환합니다. 기업 정보나 북마크를 반영한 개인화 추천은 제공하지 않습니다."
    )
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
    @Operation(
            summary = "크루 랜딩 프로젝트 목록 조회",
            description = "CREW 사용자에게 모집 중인 프로젝트 목록을 조회수 내림차순으로 반환합니다. 관심 업종, 북마크 또는 지원 이력을 반영한 개인화 추천은 제공하지 않습니다."
    )
    @GetMapping("/crew")
    public ApiResponse<List<ProjectWrapperForLandingPageDTO>> landingCrew(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<ProjectWrapperForLandingPageDTO> project = crewLandingPage.landing();
        return apiResponseFactory.success(project, userDetails);
    }
}
