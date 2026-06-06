package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.dto.crew.response.CrewDashboardResultDTO;
import com.conx.server.user.dto.crew.response.CrewProjectWorkSpaceDTO;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.crew.response.CrewWorkSpaceResponseDTO;
import com.conx.server.user.service.workspace.CrewWorkSpaceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewWorkSpaceController {

    private final CrewWorkSpaceService crewWorkSpaceService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 대시보드
     */
    @GetMapping("/dashboard")
    public ApiResponse<CrewDashboardResultDTO> getCrewDashboard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        CrewDashboardResultDTO result = crewWorkSpaceService.getCrewDashboard(customUserDetails);
        return apiResponseFactory.success(result, customUserDetails);
    }

    /**
     * 지원 현황
     */
    @GetMapping("/applications")
    public ApiResponse<CrewApplicationStatusResponseDTO> getCrewApplication(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String status
    ) {
        ApplicationBrowseFilter browseFilter = ApplicationBrowseFilter.valueOf(status);
        CrewApplicationStatusResponseDTO result = crewWorkSpaceService.getCrewApplicationStatus(
                browseFilter,
                customUserDetails
        );

        return apiResponseFactory.success(result, customUserDetails);
    }

    /**
     * 크루 워크스페이스
     */
    @GetMapping("/projects")
    public ApiResponse<CrewWorkSpaceResponseDTO> getCrewWorkSpace(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        CrewWorkSpaceResponseDTO response = crewWorkSpaceService.getCrewWorkSpace(customUserDetails);
        return apiResponseFactory.success(response, customUserDetails);
    }

    /**
     * 프로젝트 상세 워크스페이스 가져오기
     */
    @GetMapping("/projects/{projectId}")
    public ApiResponse<CrewProjectWorkSpaceDTO> getCrewDetailedProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId
    ){
        CrewProjectWorkSpaceDTO crewProjectWorkSpaceDTO =
                crewWorkSpaceService.getDetailedCrewWorkSpace(customUserDetails, projectId);

        return apiResponseFactory.success(crewProjectWorkSpaceDTO, customUserDetails);
    }

    /**
     * 결과물 제출하기
     */
    @PostMapping("/projects/{projectId}/submissions")
    public ApiResponse<?> submitResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @RequestBody SubmitProjectResultRequestDTO req
    ){
        crewWorkSpaceService.submitProjectResult(customUserDetails, projectId, req);
        return apiResponseFactory.success("결과물 제출 성공", customUserDetails);
    }

    /**
     * 결과물 임시 저장하기
     */
    @PostMapping("/projects/{projectId}/draft-submissions")
    public ApiResponse<?> draftResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @RequestBody SubmitProjectResultRequestDTO req
    ){
        crewWorkSpaceService.draftProjectResult(customUserDetails, projectId, req);
        return apiResponseFactory.success("결과물 임시 저장 성공", customUserDetails);
    }
}