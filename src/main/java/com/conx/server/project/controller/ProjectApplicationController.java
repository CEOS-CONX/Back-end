package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.service.ProjectApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectApplicationController {

    private final ProjectApplicationService projectApplicationService;

    @PostMapping("/api/v1/projects/{projectId}/applications")
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

        return ApiResponse.success("프로젝트 지원에 성공했습니다.", response);
    }

    @DeleteMapping("/api/v1/projects/{projectId}/applications/me")
    public ApiResponse<Void> cancelApplication(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        projectApplicationService.cancelApplication(projectId, userDetails.getId());

        return ApiResponse.success("프로젝트 지원 취소에 성공했습니다.", null);
    }
}