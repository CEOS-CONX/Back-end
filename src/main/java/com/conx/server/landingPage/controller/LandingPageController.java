package com.conx.server.landingPage.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.landingPage.dto.CrewWrapperDTOForDashboard;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForDashBoardDTO;
import com.conx.server.landingPage.service.CompanyLandingService;
import com.conx.server.landingPage.service.CrewLandingPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/landing")
public class LandingPageController {

    private final CompanyLandingService companyLandingService;
    private final CrewLandingPage crewLandingPage;

    public LandingPageController(CompanyLandingService companyLandingService, CrewLandingPage crewLandingPage) {
        this.companyLandingService = companyLandingService;
        this.crewLandingPage = crewLandingPage;
    }

    @GetMapping("/company")
    public ResponseEntity<ApiResponse<List<CrewWrapperDTOForDashboard>>> landingCompany(
            @RequestParam IndustryForLandingPage category
    ){
        List<CrewWrapperDTOForDashboard> crew = companyLandingService.landing(category);
        return ResponseEntity.ok(ApiResponse.success(crew));
    }

    @GetMapping("/crew")
    public ResponseEntity<ApiResponse<List<ProjectWrapperForDashBoardDTO>>> landingCrew(
            @RequestParam IndustryForLandingPage category
    ){
        List<ProjectWrapperForDashBoardDTO> project = crewLandingPage.landing(category);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
}