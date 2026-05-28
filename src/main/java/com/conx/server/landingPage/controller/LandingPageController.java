package com.conx.server.landingPage.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.landingPage.dto.AnonymousLandingPageResponseDTO;
import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.landingPage.service.AnonymousLandingPageService;
import com.conx.server.landingPage.service.CompanyLandingPageService;
import com.conx.server.landingPage.service.CrewLandingPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/landing")
public class LandingPageController {

    private final CompanyLandingPageService companyLandingService;
    private final CrewLandingPageService crewLandingPage;
    private final AnonymousLandingPageService anonymousLandingPageService;

    public LandingPageController(CompanyLandingPageService companyLandingService, CrewLandingPageService crewLandingPage, AnonymousLandingPageService anonymousLandingPageService) {
        this.companyLandingService = companyLandingService;
        this.crewLandingPage = crewLandingPage;
        this.anonymousLandingPageService = anonymousLandingPageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AnonymousLandingPageResponseDTO>> landingAnonymous(
            @RequestParam IndustryForLandingPage category
    ){
        AnonymousLandingPageResponseDTO response = anonymousLandingPageService.landing(category);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/company")
    public ResponseEntity<ApiResponse<List<CrewWrapperForLandingPageDTO>>> landingCompany(
            @RequestParam IndustryForLandingPage category
    ){
        List<CrewWrapperForLandingPageDTO> crew = companyLandingService.landing(category);
        return ResponseEntity.ok(ApiResponse.success(crew));
    }

    @GetMapping("/crew")
    public ResponseEntity<ApiResponse<List<ProjectWrapperForLandingPageDTO>>> landingCrew(
            @RequestParam IndustryForLandingPage category
    ){
        List<ProjectWrapperForLandingPageDTO> project = crewLandingPage.landing(category);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
}