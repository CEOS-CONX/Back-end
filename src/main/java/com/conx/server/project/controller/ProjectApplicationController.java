package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.CrewInfoForProjectApplicationDTO;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.service.ProjectApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectApplicationController {

    private final ProjectApplicationService projectApplicationService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping("/applications/my-info")
    public ApiResponse<CrewInfoForProjectApplicationDTO> getCrewInfoBeforeApplication(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CrewInfoForProjectApplicationDTO info = projectApplicationService.getInformationBeforeApplication(
                userDetails
        );

        return apiResponseFactory.success("프로젝트 지원 전 크루 정보를 가져오는데 성공했습니다.", info, userDetails);
    }

    /**
     * 프로젝트 지원하기
     * @param projectId 지원할 프로젝트 id
     * @param userDetails 인증정보
     * @param request 자기소개 + 제안내용으로 이루어진 body
     */
    @PostMapping("/{projectId}/applications")
    public ApiResponse<ProjectApplicationResponse> applyProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProjectApplicationRequest request
    ) {
        ProjectApplicationResponse response = projectApplicationService.applyProject(
                projectId,
                userDetails.getId(),
                request
        );

        return apiResponseFactory.success("프로젝트 지원에 성공했습니다.", response, userDetails);
    }

    /**
     * 프로젝트 지원 취소하기
     * @param projectId 지원취소할 프로젝트id
     * @param userDetails 인증정보
     */
    @DeleteMapping("/{projectId}/applications/me")
    public ApiResponse<?> cancelApplication(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        projectApplicationService.cancelApplication(projectId, userDetails.getId());

        return apiResponseFactory.success("프로젝트 지원 취소에 성공했습니다.", null);
    }
}