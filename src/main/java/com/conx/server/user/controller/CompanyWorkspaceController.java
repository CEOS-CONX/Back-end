package com.conx.server.user.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.company.response.CompanyWorkspaceDashboardResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectResponse;
import com.conx.server.user.service.workspace.CompanyWorkspaceService;
import com.conx.server.user.dto.company.request.CompanyProjectRequest;
import com.conx.server.user.dto.company.response.CompanyProjectDraftResponse;
import com.conx.server.user.dto.company.response.CompanyProjectIdResponse;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.CompanySettlementExpectedPaymentDateResponse;
import com.conx.server.user.dto.company.response.CompanySettlementResponse;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies/me")
public class CompanyWorkspaceController {

    private final CompanyWorkspaceService companyWorkspaceService;

    @GetMapping("/workspace/dashboard")
    public ApiResponse<CompanyWorkspaceDashboardResponse> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CompanyWorkspaceDashboardResponse response =
                companyWorkspaceService.getDashboard(userDetails.getId());

        return ApiResponse.success("기업 워크스페이스 대시보드 조회에 성공했습니다.", response);
    }

    @GetMapping("/projects")
    public ApiResponse<List<CompanyWorkspaceProjectResponse>> getProjects(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) ProjectType projectType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        List<CompanyWorkspaceProjectResponse> response =
                companyWorkspaceService.getProjects(
                        userDetails.getId(),
                        keyword,
                        category,
                        projectType,
                        startDate,
                        endDate
                );

        return ApiResponse.success("기업 프로젝트 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/projects/{projectId}")
    public ApiResponse<CompanyWorkspaceProjectDetailResponse> getProjectDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId
    ) {
        CompanyWorkspaceProjectDetailResponse response =
                companyWorkspaceService.getProjectDetail(userDetails.getId(), projectId);

        return ApiResponse.success("기업 프로젝트 상세 조회에 성공했습니다.", response);
    }

    @PostMapping("/projects")
    public ApiResponse<CompanyProjectIdResponse> createProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyProjectRequest request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.createProject(userDetails.getId(), request);

        return ApiResponse.success("새 프로젝트 등록에 성공했습니다.", response);
    }

    @PostMapping("/project-drafts")
    public ApiResponse<CompanyProjectIdResponse> createProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyProjectRequest request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.createProjectDraft(userDetails.getId(), request);

        return ApiResponse.success("프로젝트 임시저장에 성공했습니다.", response);
    }

    @PatchMapping("/project-drafts/{draftId}")
    public ApiResponse<CompanyProjectIdResponse> updateProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long draftId,
            @RequestBody CompanyProjectRequest request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.updateProjectDraft(userDetails.getId(), draftId, request);

        return ApiResponse.success("임시저장 프로젝트 수정에 성공했습니다.", response);
    }

    @GetMapping("/project-drafts/{draftId}")
    public ApiResponse<CompanyProjectDraftResponse> getProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long draftId
    ) {
        CompanyProjectDraftResponse response =
                companyWorkspaceService.getProjectDraft(userDetails.getId(), draftId);

        return ApiResponse.success("임시저장 프로젝트 조회에 성공했습니다.", response);
    }

    @DeleteMapping("/projects/{projectId}")
    public ApiResponse<?> deleteProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId
    ) {
        companyWorkspaceService.deleteProject(userDetails.getId(), projectId);

        return ApiResponse.success("프로젝트 삭제에 성공했습니다.");
    }

    @GetMapping("/projects/{projectId}/review")
    public ApiResponse<CompanyWorkspaceProjectDetailResponse> getProjectReviewDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId
    ) {
        CompanyWorkspaceProjectDetailResponse response =
                companyWorkspaceService.getProjectReviewDetail(userDetails.getId(), projectId);

        return ApiResponse.success("검수할 프로젝트 상세 조회에 성공했습니다.", response);
    }

    @GetMapping("/settlements")
    public ApiResponse<List<CompanySettlementResponse>> getSettlements(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) ProjectSettlementStatus status
    ) {
        List<CompanySettlementResponse> response =
                companyWorkspaceService.getSettlements(userDetails.getId(), status);

        return ApiResponse.success("정산 프로젝트 목록 조회에 성공했습니다.", response);
    }

    @PatchMapping("/settlements/{settlementId}/expected-payment-date")
    public ApiResponse<CompanySettlementExpectedPaymentDateResponse> updateSettlementExpectedPaymentDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long settlementId,
            @RequestBody CompanySettlementExpectedPaymentDateRequest request
    ) {
        CompanySettlementExpectedPaymentDateResponse response =
                companyWorkspaceService.updateSettlementExpectedPaymentDate(
                        userDetails.getId(),
                        settlementId,
                        request
                );

        return ApiResponse.success("예상 지급 날짜 설정에 성공했습니다.", response);
    }
}