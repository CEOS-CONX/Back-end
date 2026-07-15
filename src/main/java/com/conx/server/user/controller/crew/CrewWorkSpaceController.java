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
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewWorkspaceProjectStatus;
import com.conx.server.user.dto.crew.CrewWorkspaceSort;
import com.conx.server.user.dto.crew.response.CrewProjectStatusItemResponse;
import com.conx.server.user.dto.crew.response.CrewSettlementItemResponse;
import com.conx.server.user.dto.crew.CrewTodoProgressStatus;
import com.conx.server.user.dto.crew.response.CrewTodoProjectResponse;
import com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
import com.conx.server.user.dto.crew.request.CrewPaymentStatusUpdateRequest;
import com.conx.server.user.dto.crew.response.CrewPaymentStatusUpdateResponse;
import com.conx.server.user.dto.crew.response.CrewProjectWorkspaceDetailResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionListItemResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import jakarta.validation.Valid;
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
     * 크루 프로젝트 현황
     */
    @GetMapping("/projects")
    public ApiResponse<Page<CrewProjectStatusItemResponse>>
    getCrewProjects(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            CrewWorkspaceProjectStatus status,

            @RequestParam(required = false)
            Industry category,

            @RequestParam(required = false)
            ProjectType projectType,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(defaultValue = "RECENT")
            CrewWorkspaceSort sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        Page<CrewProjectStatusItemResponse> result =
                crewWorkSpaceService.getCrewProjects(
                        customUserDetails,
                        keyword,
                        status,
                        category,
                        projectType,
                        startDate,
                        endDate,
                        sort,
                        page,
                        size
                );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 크루 Todo 프로젝트 목록
     */
    @GetMapping("/todo-projects")
    public ApiResponse<Page<CrewTodoProjectResponse>>
    getCrewTodoProjects(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            CrewTodoProgressStatus progressStatus,

            @RequestParam(defaultValue = "RECENT")
            CrewWorkspaceSort sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        Page<CrewTodoProjectResponse> result =
                crewWorkSpaceService.getCrewTodoProjects(
                        customUserDetails,
                        keyword,
                        progressStatus,
                        sort,
                        page,
                        size
                );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 크루 워크스페이스
     */
    @GetMapping("/workSpace")
    public ApiResponse<CrewWorkSpaceResponseDTO> getCrewWorkSpace(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        CrewWorkSpaceResponseDTO response = crewWorkSpaceService.getCrewWorkSpace(customUserDetails);
        return apiResponseFactory.success(response, customUserDetails);
    }

    /**
     * 프로젝트 상세 워크스페이스 가져오기
     */
    @GetMapping("/workSpace/{projectId}")
    public ApiResponse<CrewProjectWorkSpaceDTO> getCrewDetailedProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId
    ){
        CrewProjectWorkSpaceDTO crewProjectWorkSpaceDTO =
                crewWorkSpaceService.getDetailedCrewWorkSpace(customUserDetails, projectId);

        return apiResponseFactory.success(crewProjectWorkSpaceDTO, customUserDetails);
    }

    /**
     * 신규 프로젝트 작업 상세
     */
    @GetMapping(
            "/projects/{projectId}/workspace"
    )
    public ApiResponse<CrewProjectWorkspaceDetailResponse>
    getCrewProjectWorkspaceDetail(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @PathVariable
            long projectId
    ) {
        CrewProjectWorkspaceDetailResponse result =
                crewWorkSpaceService
                        .getCrewProjectWorkspaceDetail(
                                customUserDetails,
                                projectId
                        );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 프로젝트 결과물 공유 이력
     */
    @GetMapping(
            "/projects/{projectId}/submissions"
    )
    public ApiResponse<
            Page<CrewProjectSubmissionListItemResponse>
            >
    getCrewProjectSubmissions(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @PathVariable
            long projectId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        Page<CrewProjectSubmissionListItemResponse> result =
                crewWorkSpaceService
                        .getCrewProjectSubmissions(
                                customUserDetails,
                                projectId,
                                page,
                                size
                        );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 프로젝트 결과물 공유 상세
     */
    @GetMapping(
            "/projects/{projectId}/submissions/{submissionId}"
    )
    public ApiResponse<CrewProjectSubmissionDetailResponse>
    getCrewProjectSubmissionDetail(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @PathVariable
            long projectId,

            @PathVariable
            long submissionId
    ) {
        CrewProjectSubmissionDetailResponse result =
                crewWorkSpaceService
                        .getCrewProjectSubmissionDetail(
                                customUserDetails,
                                projectId,
                                submissionId
                        );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 결과물 제출하기
     */
    @PostMapping("/projects/{projectId}/submissions")
    public ApiResponse<?> submitResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @Valid @RequestBody SubmitProjectResultRequestDTO req
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

    /**
     * 크루 정산 요약
     */
    @GetMapping("/settlements/summary")
    public ApiResponse<CrewSettlementSummaryResponse>
    getCrewSettlementSummary(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails
    ) {
        CrewSettlementSummaryResponse result =
                crewWorkSpaceService
                        .getCrewSettlementSummary(
                                customUserDetails
                        );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 크루 정산 목록
     */
    @GetMapping("/settlements")
    public ApiResponse<Page<CrewSettlementItemResponse>>
    getCrewSettlements(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            ProjectSettlementStatus settlementStatus,

            @RequestParam(required = false)
            CrewPaymentStatus paymentStatus,

            @RequestParam(required = false)
            Industry category,

            @RequestParam(required = false)
            ProjectType projectType,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate settlementStartDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate settlementEndDate,

            @RequestParam(defaultValue = "RECENT")
            CrewWorkspaceSort sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        Page<CrewSettlementItemResponse> result =
                crewWorkSpaceService.getCrewSettlements(
                        customUserDetails,
                        keyword,
                        settlementStatus,
                        paymentStatus,
                        category,
                        projectType,
                        startDate,
                        endDate,
                        settlementStartDate,
                        settlementEndDate,
                        sort,
                        page,
                        size
                );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }

    /**
     * 크루 지급 확인 상태 변경
     */
    @PatchMapping(
            "/settlements/{settlementId}/payment-status"
    )
    public ApiResponse<CrewPaymentStatusUpdateResponse>
    updateCrewPaymentStatus(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @PathVariable
            Long settlementId,

            @Valid
            @RequestBody
            CrewPaymentStatusUpdateRequest request
    ) {
        CrewPaymentStatusUpdateResponse result =
                crewWorkSpaceService
                        .updateCrewPaymentStatus(
                                customUserDetails,
                                settlementId,
                                request
                        );

        return apiResponseFactory.success(
                result,
                customUserDetails
        );
    }
}