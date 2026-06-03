package com.conx.server.user.controller.crew;

import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.dto.crew.response.CrewDashboardResultDTO;
import com.conx.server.user.dto.crew.response.CrewProjectWorkSpaceDTO;
import com.conx.server.user.service.workspace.CrewApplicationStatusService;
import com.conx.server.user.service.workspace.CrewDashboardService;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.service.workspace.CrewWorkSpaceService;
import com.conx.server.user.dto.crew.response.CrewParticipatedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewProjectRewardResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionResponse;
import org.springframework.data.domain.Page;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crews")
public class CrewWorkSpaceController {

    private final CrewDashboardService crewDashboardService;
    private final CrewApplicationStatusService crewApplicationStatusService;
    private final CrewWorkSpaceService crewWorkSpaceService;

    public CrewWorkSpaceController(CrewDashboardService crewDashboardService, CrewApplicationStatusService crewApplicationStatusService, CrewWorkSpaceService crewWorkSpaceService) {
        this.crewDashboardService = crewDashboardService;
        this.crewApplicationStatusService = crewApplicationStatusService;
        this.crewWorkSpaceService = crewWorkSpaceService;
    }

    /**
     * 대시보드
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<CrewDashboardResultDTO>> getCrewDashboard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        CrewDashboardResultDTO result = crewDashboardService.getCrewDashboard(customUserDetails);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 지원 현황
     */
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<CrewApplicationStatusResponseDTO>> getCrewApplication(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String status
    ) {
        ApplicationBrowseFilter browseFilter = ApplicationBrowseFilter.valueOf(status);
        CrewApplicationStatusResponseDTO result = crewApplicationStatusService.getCrewApplicationStatus(
                browseFilter,
                customUserDetails
        );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 프로젝트 상세 워크스페이스 가져오기
     */
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ApiResponse<CrewProjectWorkSpaceDTO>> getCrewDetailedProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId
    ){
        CrewProjectWorkSpaceDTO crewProjectWorkSpaceDTO =
                crewWorkSpaceService.getCrewWorkSpace(customUserDetails, projectId);

        return ResponseEntity.ok(ApiResponse.success(crewProjectWorkSpaceDTO));
    }

    /**
     * 결과물 제출하기
     */
    @PostMapping("/projects/{projectId}/submissions")
    public ResponseEntity<ApiResponse<?>> submitResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @RequestBody SubmitProjectResultRequestDTO req
    ){
        crewWorkSpaceService.submitProjectResult(customUserDetails, projectId, req);
        return ResponseEntity.ok(ApiResponse.success("결과물 제출 성공"));
    }

    /**
     * 결과물 임시 저장하기
     */
    @PostMapping("/projects/{projectId}/draft-submissions")
    public ResponseEntity<ApiResponse<?>> draftResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @RequestBody SubmitProjectResultRequestDTO req
    ){
        crewWorkSpaceService.draftProjectResult(customUserDetails, projectId, req);
        return ResponseEntity.ok(ApiResponse.success("결과물 임시 저장 성공"));
    }

    /**
     * 내 참여 프로젝트 목록 조회
     */
    @GetMapping("/me/projects")
    public ResponseEntity<ApiResponse<Page<CrewParticipatedProjectResponse>>> getMyProjects(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProjectStatus status
    ) {
        Page<CrewParticipatedProjectResponse> result =
                crewWorkSpaceService.getMyProjects(customUserDetails, page, size, status);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 내 결과물 조회
     */
    @GetMapping("/me/projects/{projectId}/submissions")
    public ResponseEntity<ApiResponse<CrewProjectSubmissionResponse>> getMySubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId
    ) {
        CrewProjectSubmissionResponse result =
                crewWorkSpaceService.getMySubmission(customUserDetails, projectId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 제출한 결과물 수정
     */
    @PatchMapping("/me/projects/{projectId}")
    public ResponseEntity<ApiResponse<?>> updateSubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @RequestBody SubmitProjectResultRequestDTO req
    ) {
        crewWorkSpaceService.updateSubmission(customUserDetails, projectId, req);

        return ResponseEntity.ok(ApiResponse.success("결과물 수정 성공"));
    }

    /**
     * 리워드 상태 조회
     */
    @GetMapping("/me/projects/{projectId}/rewards")
    public ResponseEntity<ApiResponse<CrewProjectRewardResponse>> getReward(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId
    ) {
        CrewProjectRewardResponse result =
                crewWorkSpaceService.getReward(customUserDetails, projectId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}