package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ProjectBrowseSort;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.service.ProjectBrowseService;
import com.conx.server.user.domain.types.Industry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectBrowseController {

    private final ProjectBrowseService projectBrowseService;

    @GetMapping
    public ApiResponse<Page<ProjectBrowseResponse>> getProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) ProjectType projectType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) ProjectBrowseSort sort,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<ProjectBrowseResponse> response = projectBrowseService.getProjects(
                keyword,
                category,
                projectType,
                startDate,
                endDate,
                sort,
                page,
                size
        );

        return ApiResponse.success("프로젝트 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/{projectId}")
    public ApiResponse<ProjectBrowseDetailResponse> getProjectDetail(
            @PathVariable Long projectId
    ) {
        ProjectBrowseDetailResponse response = projectBrowseService.getProjectDetail(projectId);

        return ApiResponse.success("프로젝트 상세 조회에 성공했습니다.", response);
    }
}