package com.conx.server.user.controller.admin;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.user.dto.admin.response.AdminProjectContractCompleteResponse;
import com.conx.server.user.service.admin.AdminProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final AdminProjectService adminProjectService;
    private final ApiResponseFactory apiResponseFactory;

    @Operation(
            summary = "프로젝트 계약 완료 처리",
            description = "ADMIN 권한으로 선정 크루가 있는 CONTRACT_PENDING 프로젝트의 계약 완료를 처리하며, 프로젝트 상태를 PROGRESS로 변경합니다."
    )
    @PatchMapping("/{projectId}/contract-complete")
    public ApiResponse<AdminProjectContractCompleteResponse> completeContract(
            @PathVariable Long projectId
    ) {
        AdminProjectContractCompleteResponse response =
                adminProjectService.completeContract(projectId);

        return apiResponseFactory.success(response, null);
    }
}
