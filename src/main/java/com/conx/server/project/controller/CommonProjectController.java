package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.service.CommonProjectService;
import com.conx.server.user.dto.company.response.ProjectInspectionWrapperDTO;
import com.conx.server.user.dto.company.response.ProjectStatusResponseDTO;
import com.conx.server.user.service.workspace.CompanyWorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class CommonProjectController {
    private final ApiResponseFactory apiResponseFactory;
    private final CommonProjectService commonProjectService;

    @Operation(
            summary = "프로젝트 결과물 및 피드백 상세 조회",
            description = "로그인한 사용자가 특정 프로젝트의 결과물 제출 내역과 피드백 상세 정보를 조회합니다."
    )
    @GetMapping("/{projectId}/submissions/{submissionId}")
    public ApiResponse<ProjectInspectionWrapperDTO> getProjectReviewDetail(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long projectId,

            @PathVariable
            Long submissionId
    ) {
        ProjectInspectionWrapperDTO response =
                commonProjectService.getProjectReviewDetail(
                        userDetails.getUserEmail(),
                        projectId,
                        submissionId
                );

        return apiResponseFactory.success(
                "상세 결과물 공유내역 조회에 성공했습니다.",
                response,
                userDetails
        );
    }
}
