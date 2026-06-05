package com.conx.server.user.controller.admin;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.user.dto.admin.response.AdminProjectContractCompleteResponse;
import com.conx.server.user.service.admin.AdminProjectService;
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

    @PatchMapping("/{projectId}/contract-complete")
    public ResponseEntity<ApiResponse<AdminProjectContractCompleteResponse>> completeContract(
            @PathVariable Long projectId
    ) {
        AdminProjectContractCompleteResponse response =
                adminProjectService.completeContract(projectId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}