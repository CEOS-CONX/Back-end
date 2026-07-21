package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.ProjectStatusFilter;
import com.conx.server.user.dto.company.request.CompanyFeedbackRequestDTO;
import com.conx.server.user.dto.company.request.CompanyProjectEvaluationRequest;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.request.CompanySettlementCompleteRequest;
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

    /**
     * 기업 워크스페이스 대시보드 조회
     */
    @GetMapping("/workspace/dashboard")
    public ApiResponse<CompanyWorkspaceDashboardResponse> getDashboard(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @RequestParam(required = false)
            ProjectStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        CompanyWorkspaceDashboardResponse response =
                companyWorkspaceService.getDashboard(
                        userDetails.getId(),
                        status,
                        startDate,
                        endDate,
                        pageable
                );

        return apiResponseFactory.success(
                "기업 워크스페이스 대시보드 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 기업 프로젝트 목록 조회
     */
    @GetMapping("/projects")
    public ApiResponse<Page<CompanyWorkspaceProjectResponse>> getProjects(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProjectStatusFilter status,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) CrewType crewType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(
                    size = 12,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        Page<CompanyWorkspaceProjectResponse> response =
                companyWorkspaceService.getProjects(
                        userDetails.getId(),
                        keyword,
                        status,
                        category,
                        crewType,
                        startDate,
                        endDate,
                        pageable
                );

        return apiResponseFactory.success(
                "기업 프로젝트 목록 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 파트너 크루 조회
     */
    @GetMapping("/projects/partner-crew")
    public ApiResponse<Page<CompanyPartnerCrewResponse>> getPartnerCrew(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProjectStatusFilter status,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) CrewType crewType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CompanyPartnerCrewResponse> response = companyWorkspaceService.getPartnerCrew(
                userDetails.getId(), keyword, status, category, crewType, startDate, endDate, pageable);

        return apiResponseFactory.success(
                "파트너 크루 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 기업 프로젝트 상세 조회
     */
    @GetMapping("/projects/{projectId}")
    public ApiResponse<CompanyWorkspaceProjectDetailResponse> getProjectDetail(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {
        CompanyWorkspaceProjectDetailResponse response =
                companyWorkspaceService.getProjectDetail(
                        userDetails.getId(),
                        projectId,
                        page,
                        size
                );

        return apiResponseFactory.success(
                "기업 프로젝트 상세 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 프로젝트 등록
     */
    @PostMapping("/projects")
    public ApiResponse<CompanyProjectIdResponse> createProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CompanyProjectRequestDTO request,
            @RequestParam boolean isDraft
    ) {
        CompanyProjectIdResponse response = companyWorkspaceService.createProject(userDetails.getId(),
                request, isDraft);

        return apiResponseFactory.success(
                "새 프로젝트 등록에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 임시저장한 프로젝트가 있는지 여부 조사
     */
    @GetMapping("/projects/hasDraft")
    public ApiResponse<Boolean> hasDraft(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        Boolean hasDraft = companyWorkspaceService.findDraft(customUserDetails.getId());
        return apiResponseFactory.success("임시저장 프로젝트 존재여부 확인에 성공했습니다.", hasDraft, customUserDetails);
    }

    /**
     * 프로젝트 수정
     */
    @PatchMapping("/projects/{projectId}")
    public ApiResponse<CompanyProjectIdResponse> updateProject(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId,

            @Valid
            @RequestBody
            CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.updateProject(
                        userDetails.getId(),
                        projectId,
                        request
                );

        return apiResponseFactory.success(
                "프로젝트 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 프로젝트 삭제
     */
    @DeleteMapping("/projects/{projectId}")
    public ApiResponse<?> deleteProject(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId
    ) {
        companyWorkspaceService.deleteProject(
                userDetails.getId(),
                projectId
        );

        return apiResponseFactory.success(
                "프로젝트 삭제에 성공했습니다.",
                userDetails
        );
    }

    /**
     * 프로젝트 임시 저장
     */
    @PostMapping("/project-drafts")
    public ApiResponse<CompanyProjectIdResponse> createProjectDraft(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @RequestBody
            CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.createProjectDraft(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "프로젝트 임시저장에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 임시 저장 프로젝트 수정
     */
    @PatchMapping("/project-drafts")
    public ApiResponse<CompanyProjectIdResponse> updateProjectDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            //@PathVariable Long draftId,
            @RequestBody CompanyProjectRequestDTO request
    ) {
        CompanyProjectIdResponse response =
                companyWorkspaceService.updateProjectDraft(
                        userDetails.getId(),
                        //draftId,
                        request
                );

        return apiResponseFactory.success(
                "임시저장 프로젝트 수정에 성공했습니다.",
                response,
                userDetails
        );
    }


    /**
     * 임시 저장 프로젝트 상세 조회
     */
    @GetMapping("/project-drafts")
    public ApiResponse<CompanyProjectDraftResponse> getProjectDraft(
            @AuthenticationPrincipal
            CustomUserDetails userDetails

//            @PathVariable
//            Long draftId
    ) {
        CompanyProjectDraftResponse response =
                companyWorkspaceService.getProjectDraft(
                        userDetails.getId()
                        //draftId
                );

        return apiResponseFactory.success(
                "임시저장 프로젝트 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 결과물 및 피드백 상세 조회
     */
    @GetMapping("/projects/{projectId}/submissions/{submissionId}")
    public ApiResponse<ProjectInspectionWrapperDTO> getProjectReviewDetail(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId,

            @PathVariable
            Long submissionId
    ) {
        ProjectInspectionWrapperDTO response =
                companyWorkspaceService.getProjectReviewDetail(
                        userDetails.getId(),
                        projectId,
                        submissionId
                );

        return apiResponseFactory.success(
                "상세 결과물 공유내역 조회에 성공했습니다.",
                response,
                userDetails
        );
    }



    /**
     * 프로젝트 기준 충족 버튼 클릭
     */
    @PostMapping("/projects/{projectId}/criteria/{criteriaId}")
    public ApiResponse<?> checkCriteria(@PathVariable Long projectId,
                                        @PathVariable long criteriaId,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails){
        companyWorkspaceService.checkCriteria(projectId, customUserDetails.getId(), criteriaId);
        return apiResponseFactory.success("프로젝트 기준 충족 버튼을 클릭하는데 성공했습니다.", customUserDetails);
    }

    /**
     * 결과물 피드백 등록
     */
    @PostMapping("/projects/{projectId}/submissions/{submissionId}/feedback")
    public ApiResponse<ProjectInspectionWrapperDTO> registerFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @Valid @RequestBody CompanyFeedbackRequestDTO request
    ) {
        ProjectInspectionWrapperDTO response =
                companyWorkspaceService.registerFeedBack(
                        userDetails.getId(),
                        submissionId,
                        request
                );

        return apiResponseFactory.success(
                "피드백 등록에 성공했습니다.",
                response,
                userDetails
        );
    }


    /**
     * 프로젝트 참여 크루 선정
     */
    @PostMapping(
            "/projects/{projectId}/applications/{applicationId}/select"
    )
    public ApiResponse<CompanyProjectApplicationSelectResponse> selectProjectApplication(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId,

            @PathVariable
            Long applicationId
    ) {
        CompanyProjectApplicationSelectResponse response =
                companyWorkspaceService.selectProjectApplication(
                        userDetails.getId(),
                        projectId,
                        applicationId
                );

        return apiResponseFactory.success(
                "프로젝트 참여 크루 선정에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 프로젝트 평가 등록
     */
    @PostMapping("/projects/{projectId}/evaluation")
    public ApiResponse<CompanyProjectEvaluationResponse> evaluateProject(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId,

            @Valid
            @RequestBody
            CompanyProjectEvaluationRequest request
    ) {
        CompanyProjectEvaluationResponse response =
                companyWorkspaceService.evaluateProject(
                        userDetails.getId(),
                        projectId,
                        request
                );

        return apiResponseFactory.success(
                "크루 평가 등록에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 기존 기업 정산 목록 조회
     */
    @GetMapping("/settlements")
    public ApiResponse<List<CompanySettlementResponse>> getSettlements(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @RequestParam(required = false)
            ProjectSettlementStatus status
    ) {
        List<CompanySettlementResponse> response =
                companyWorkspaceService.getSettlements(
                        userDetails.getId(),
                        status
                );

        return apiResponseFactory.success(
                "정산 프로젝트 목록 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 정산 예정 지급일 수정
     */
    @PatchMapping(
            "/settlements/{settlementId}/expected-payment-date"
    )
    public ApiResponse<CompanySettlementExpectedPaymentDateResponse>
    updateSettlementExpectedPaymentDate(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long settlementId,

            @Valid
            @RequestBody
            CompanySettlementExpectedPaymentDateRequest request
    ) {
        CompanySettlementExpectedPaymentDateResponse response =
                companyWorkspaceService
                        .updateSettlementExpectedPaymentDate(
                                userDetails.getId(),
                                settlementId,
                                request
                        );

        return apiResponseFactory.success(
                "예상 지급 날짜 설정에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 정산 완료 처리
     */
    @PatchMapping("/settlements/{settlementId}/complete")
    public ApiResponse<CompanySettlementCompleteResponse>
    completeSettlement(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long settlementId,

            @Valid
            @RequestBody
            CompanySettlementCompleteRequest request
    ) {
        CompanySettlementCompleteResponse response =
                companyWorkspaceService.completeSettlement(
                        userDetails.getId(),
                        settlementId,
                        request
                );

        return apiResponseFactory.success(
                "정산 지급 완료 처리에 성공했습니다.",
                response,
                userDetails
        );
    }

    /**
     * 기업 정산 관리 현황 조회
     */
    @GetMapping("/adjustment")
    public ApiResponse<SubsidyStatusResponse>
    getCompanySubsidyStatus(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @RequestParam(required = false)
            ProjectSettlementStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        SubsidyStatusResponse response =
                companyWorkspaceService.getCompanySubsidyStatus(
                        userDetails.getId(),
                        status,
                        startDate,
                        endDate,
                        pageable
                );

        return apiResponseFactory.success(
                "정산 현황 조회에 성공했습니다.",
                response,
                userDetails
        );
    }
}
