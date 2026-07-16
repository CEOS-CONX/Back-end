package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyFeedbackRequestDTO;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.*;
import com.conx.server.user.service.workspace.CompanyWorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies/me")
public class CompanyWorkspaceController {

    private final CompanyWorkspaceService companyWorkspaceService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping("/workspace/dashboard")
    public ApiResponse<CompanyWorkspaceDashboardResponse> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        CompanyWorkspaceDashboardResponse response =
                companyWorkspaceService.getDashboard(userDetails.getId(), status, startDate, endDate, pageable);

        return apiResponseFactory.success("기업 워크스페이스 대시보드 조회에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/projects")
    public ApiResponse<Page<CompanyWorkspaceProjectResponse>> getProjects(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) CrewType crewType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CompanyWorkspaceProjectResponse> response =
                companyWorkspaceService.getProjects(
                        userDetails.getId(),
                        keyword,
                        category,
                        crewType,
                        startDate,
                        endDate,
                        pageable
                );

        return apiResponseFactory.success("기업 프로젝트 목록 조회에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/projects/{projectId}")
    public ApiResponse<CompanyWorkspaceProjectDetailResponse> getProjectDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        CompanyWorkspaceProjectDetailResponse response =
                companyWorkspaceService.getProjectDetail(userDetails.getId(), projectId, page, size);

        return apiResponseFactory.success("기업 프로젝트 상세 조회에 성공했습니다.", response, userDetails);
    }

    @PostMapping("/projects")
    public ApiResponse<CompanyProjectIdResponse> createProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.createProject(userDetails.getId(), request);

        return apiResponseFactory.success("새 프로젝트 등록에 성공했습니다.", response, userDetails);
    }

    @PatchMapping("/projects/{projectId}")
    public ApiResponse<CompanyProjectIdResponse> updateProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @Valid @RequestBody CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.updateProject(userDetails.getId(), projectId, request);

        return apiResponseFactory.success("프로젝트 수정에 성공했습니다.", response, userDetails);
    }

    @DeleteMapping("/projects/{projectId}")
    public ApiResponse<?> deleteProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId
    ) {
        companyWorkspaceService.deleteProject(userDetails.getId(), projectId);

        return apiResponseFactory.success("프로젝트 삭제에 성공했습니다.", userDetails);
    }

    @PostMapping("/project-drafts")
    public ApiResponse<CompanyProjectIdResponse> createProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.createProjectDraft(userDetails.getId(), request);

        return apiResponseFactory.success("프로젝트 임시저장에 성공했습니다.", response, userDetails);
    }

    @PatchMapping("/project-drafts/{draftId}")
    public ApiResponse<CompanyProjectIdResponse> updateProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long draftId,
            @RequestBody CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.updateProjectDraft(userDetails.getId(), draftId, request);

        return apiResponseFactory.success("임시저장 프로젝트 수정에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/project-drafts/{draftId}")
    public ApiResponse<CompanyProjectDraftResponse> getProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long draftId
    ) {
        CompanyProjectDraftResponse response =
                companyWorkspaceService.getProjectDraft(userDetails.getId(), draftId);

        return apiResponseFactory.success("임시저장 프로젝트 조회에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/projects/{projectId}/review/{submissionId}")
    public ApiResponse<ProjectInspectionWrapperDTO> getProjectReviewDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long submissionId) {
        ProjectInspectionWrapperDTO response =
                companyWorkspaceService.getProjectReviewDetail(userDetails.getId(), projectId, submissionId);

        return apiResponseFactory.success("상세 결과물 공유내역 조회에 성공했습니다.", response, userDetails);
    }

    @PostMapping("/projects/{projectId}/applications/{applicationId}/select")
    public ApiResponse<CompanyProjectApplicationSelectResponse> selectProjectApplication(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long applicationId
    ) {
        CompanyProjectApplicationSelectResponse response =
                companyWorkspaceService.selectProjectApplication(
                        userDetails.getId(),
                        projectId,
                        applicationId
                );

        return apiResponseFactory.success("프로젝트 참여 크루 선정에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/projects/{projectId}/partner-crew")
    public ApiResponse<CompanyPartnerCrewResponse> getPartnerCrew(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId
    ) {
        CompanyPartnerCrewResponse response =
                companyWorkspaceService.getPartnerCrew(userDetails.getId(), projectId);

        return apiResponseFactory.success("파트너 크루 조회에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/settlements")
    public ApiResponse<List<CompanySettlementResponse>> getSettlements(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) ProjectSettlementStatus status
    ) {
        List<CompanySettlementResponse> response =
                companyWorkspaceService.getSettlements(userDetails.getId(), status);

        return apiResponseFactory.success("정산 프로젝트 목록 조회에 성공했습니다.", response, userDetails);
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

        return apiResponseFactory.success("예상 지급 날짜 설정에 성공했습니다.", response, userDetails);
    }

    /**
     * 피드백 달기
     */
    @GetMapping("//projects/{projectId}/review/{submissionId}/feedback")
    public ApiResponse<ProjectInspectionWrapperDTO> registerFeedback(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CompanyFeedbackRequestDTO req,
            @PathVariable Long submissionId) {
        ProjectInspectionWrapperDTO response = companyWorkspaceService.registerFeedBack(customUserDetails.getId(), submissionId, req);

        return apiResponseFactory.success(
                "피드백 등록에 성공했습니다.", response, customUserDetails
        );
    }

    /**
     * 정산관리
     */
    @GetMapping("/adjustment")
    public ApiResponse<SubsidyStatusResponse> getCompanySubsidyStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) ProjectSettlementStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        SubsidyStatusResponse response = companyWorkspaceService.getCompanySubsidyStatus(customUserDetails.getId(), status, startDate, endDate, pageable);

        return apiResponseFactory.success(
                "정산 현황 조회에 성공했습니다.", response, customUserDetails
        );
    }
}