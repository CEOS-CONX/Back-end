package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.response.ProjectStatusResponseDTO;
import com.conx.server.user.dto.crew.CrewTodoProgressStatus;
import com.conx.server.user.dto.crew.CrewWorkspaceProjectStatus;
import com.conx.server.user.dto.crew.CrewWorkspaceSort;
import com.conx.server.user.dto.crew.request.CrewPaymentStatusUpdateRequest;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.dto.crew.response.CrewDashboardResultDTO;
import com.conx.server.user.dto.crew.response.CrewPaymentStatusUpdateResponse;
import com.conx.server.user.dto.crew.response.CrewProjectStatusItemResponse;
import com.conx.server.user.dto.crew.response.CrewProjectWorkSpaceDTO;
import com.conx.server.user.dto.crew.response.CrewSettlementItemResponse;
import com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse;
import com.conx.server.user.dto.crew.response.CrewTodoProjectResponse;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.crew.response.CrewWorkSpaceResponseDTO;
import com.conx.server.user.service.workspace.CrewWorkSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewWorkSpaceController {

    private final CrewWorkSpaceService crewWorkSpaceService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 대시보드
     */
    @Operation(
            summary = "크루 대시보드 조회",
            description = "로그인한 크루의 누적 지급액, 평가 점수, 단계별 프로젝트 수와 최근 미완료 Todo를 조회합니다."
    )
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
    @Operation(
            summary = "크루 프로젝트 지원 현황 조회",
            description = "로그인한 크루의 프로젝트 지원 현황을 조회합니다. status는 PENDING, SELECTED, REJECTED, ALL 중 하나를 필수로 전달해야 합니다."
    )
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

    @Operation(
            summary = "크루 프로젝트 현황 목록 조회",
            description = "로그인한 크루의 프로젝트 현황을 페이지 단위로 조회합니다. 검색어, 상태, 업종, 프로젝트 유형, 기간과 정렬 조건을 적용할 수 있습니다."
    )
    @GetMapping("/projects")
    public ApiResponse<Page<CrewProjectStatusItemResponse>> getCrewProjects(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CrewWorkspaceProjectStatus status,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) ProjectType projectType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "RECENT") CrewWorkspaceSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return apiResponseFactory.success(
                crewWorkSpaceService.getCrewProjects(
                        userDetails, keyword, status, category, projectType,
                        startDate, endDate, sort, page, size
                ),
                userDetails
        );
    }

    @Operation(
            summary = "크루 프로젝트 Todo 목록 조회",
            description = "로그인한 크루의 프로젝트 Todo 목록을 페이지 단위로 조회합니다. 검색어, 진행 상태와 정렬 조건을 적용할 수 있습니다."
    )
    @GetMapping("/todo-projects")
    public ApiResponse<Page<CrewTodoProjectResponse>> getCrewTodoProjects(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CrewTodoProgressStatus progressStatus,
            @RequestParam(defaultValue = "RECENT") CrewWorkspaceSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return apiResponseFactory.success(
                crewWorkSpaceService.getCrewTodoProjects(
                        userDetails, keyword, progressStatus, sort, page, size
                ),
                userDetails
        );
    }

    /**
     * 크루 워크스페이스
     */
    @Operation(
            summary = "크루 워크스페이스 목록 조회",
            description = "로그인한 크루가 선정된 프로젝트 목록과 각 프로젝트의 결과물 제출 마감일까지 남은 기간을 조회합니다."
    )
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
    @Operation(
            summary = "크루 프로젝트 워크스페이스 상세 조회",
            description = "로그인한 크루가 선정된 프로젝트의 상세 정보와 결과물 제출·검수 이력을 조회합니다. 계약 전이거나 종료된 프로젝트는 조회할 수 없습니다."
    )
    @GetMapping("/workSpace/{projectId}")
    public ApiResponse<ProjectStatusResponseDTO> getCrewDetailedProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        ProjectStatusResponseDTO projectStatusDTO =
                crewWorkSpaceService.getProjectDetail(customUserDetails.getId(), projectId, page, size);

        return apiResponseFactory.success(projectStatusDTO, customUserDetails);
    }

    /**
     * 결과물 제출하기
     */
    @Operation(
            summary = "크루 프로젝트 결과물 제출",
            description = "로그인한 크루가 수행 중인 프로젝트의 결과물을 제출합니다. 제출 후 프로젝트는 검수 상태로 변경되고 관련 Todo 완료 및 알림 처리가 수행됩니다."
    )
    @PostMapping("/projects/{projectId}/submissions")
    public ApiResponse<?> submitResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long projectId,
            @Valid @RequestBody SubmitProjectResultRequestDTO req
    ){
        crewWorkSpaceService.submitProjectResult(customUserDetails, projectId, req);
        return apiResponseFactory.success("결과물 제출 성공", customUserDetails);
    }

    @Operation(
            summary = "크루 정산 요약 조회",
            description = "로그인한 크루의 지급 완료 금액, 지급 대기 금액, 이번 달 지급액과 다음 예정 지급일을 조회합니다."
    )
    @GetMapping("/settlements/summary")
    public ApiResponse<CrewSettlementSummaryResponse> getCrewSettlementSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return apiResponseFactory.success(
                crewWorkSpaceService.getCrewSettlementSummary(userDetails),
                userDetails
        );
    }

    @Operation(
            summary = "크루 정산 목록 조회",
            description = "로그인한 크루의 정산 목록을 페이지 단위로 조회합니다. 검색어, 정산 상태, 지급 확인 상태, 업종, 프로젝트 유형과 기간 조건을 적용할 수 있습니다."
    )
    @GetMapping("/settlements")
    public ApiResponse<Page<CrewSettlementItemResponse>> getCrewSettlements(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProjectSettlementStatus settlementStatus,
            @RequestParam(required = false) CrewPaymentStatus paymentStatus,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) ProjectType projectType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate settlementStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate settlementEndDate,
            @RequestParam(defaultValue = "RECENT") CrewWorkspaceSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return apiResponseFactory.success(
                crewWorkSpaceService.getCrewSettlements(
                        userDetails, keyword, settlementStatus, paymentStatus,
                        category, projectType, startDate, endDate,
                        settlementStartDate, settlementEndDate, sort, page, size
                ),
                userDetails
        );
    }

    @Operation(
            summary = "크루 지급 확인 상태 변경",
            description = "로그인한 크루가 본인 정산 건의 지급 확인 상태를 BEFORE_PAYMENT 또는 PAYMENT_CONFIRMED로 변경합니다. 이 처리는 실제 정산 상태나 지급일을 변경하지 않습니다."
    )
    @PatchMapping("/settlements/{settlementId}/payment-status")
    public ApiResponse<CrewPaymentStatusUpdateResponse> updateCrewPaymentStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long settlementId,
            @Valid @RequestBody CrewPaymentStatusUpdateRequest request
    ) {
        return apiResponseFactory.success(
                crewWorkSpaceService.updateCrewPaymentStatus(
                        userDetails, settlementId, request
                ),
                userDetails
        );
    }
}
