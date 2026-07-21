package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.CrewInfoForProjectApplicationDTO;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.service.ProjectApplicationService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "프로젝트 지원 전 크루 정보 조회",
            description = "로그인한 활성 크루의 크루명, 담당자명, 최종 수정 일자와 정보 수정 완료 여부를 조회합니다. 특정 프로젝트의 지원 가능 여부나 기존 지원 상태는 확인하지 않습니다."
    )
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
    @Operation(
            summary = "프로젝트 지원",
            description = "활성 CREW가 모집 중인 프로젝트에 지원 동기(motivation)를 제출합니다. motivation은 공백일 수 없으며, 해당 프로젝트에 지원 이력이 남아 있으면 상태와 관계없이 중복 지원할 수 없습니다."
    )
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
    @Operation(
            summary = "프로젝트 지원 취소",
            description = "CREW 본인의 PENDING 상태 지원서를 취소하고 실제 삭제합니다. 삭제 후 프로젝트가 계속 모집 중이면 다시 지원할 수 있으며, 선정 또는 거절된 지원서는 취소할 수 없습니다."
    )
    @DeleteMapping("/{projectId}/applications/me")
    public ApiResponse<?> cancelApplication(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        projectApplicationService.cancelApplication(projectId, userDetails.getId());

        return apiResponseFactory.success("프로젝트 지원 취소에 성공했습니다.", null);
    }
}
