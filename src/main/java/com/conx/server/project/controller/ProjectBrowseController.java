package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ProjectBrowseSort;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.service.ProjectBrowseService;
import com.conx.server.user.domain.types.Industry;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 프로젝트 목록조회하기
     * @param keyword 검색어
     * @param category 카테고리
     * @param projectType 프로젝트유형
     * @param startDate 검색필터링 시작일
     * @param endDate 검색필터링 종료일
     * @param sort 정렬기준
     */
    @Operation(
            summary = "모집 중인 프로젝트 목록 조회",
            description = "모집 중인 프로젝트를 검색·필터링하여 페이지 단위로 조회합니다. page와 size는 필수이며, sort 기본값은 RECENT이고 비로그인 사용자의 북마크 여부는 항상 false입니다."
    )
    @GetMapping
    public ApiResponse<Page<ProjectBrowseResponse>> getProjects(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) Industry category,
            @RequestParam(name = "projectType", required = false) ProjectType projectType,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "sort", required = false) ProjectBrowseSort sort,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Page<ProjectBrowseResponse> response = projectBrowseService.getProjects(
                keyword,
                category,
                projectType,
                startDate,
                endDate,
                sort,
                page,
                size,
                userDetails
        );

        return apiResponseFactory.success("프로젝트 목록 조회에 성공했습니다.", response, null);
    }

    /**
     * 프로젝트 상세조회하기
     * @param projectId 조회할 프로젝트id
     */
    @Operation(
            summary = "모집 중인 프로젝트 상세 조회",
            description = "로그인 사용자가 모집 중인 프로젝트 상세와 질문 목록을 조회합니다. page와 size는 질문 목록에 적용되며, mine=true이면 본인이 작성한 질문만 반환하고 비밀 질문은 작성자·프로젝트 기업·관리자에게만 공개됩니다."
    )
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectBrowseDetailResponse> getProjectDetail(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean mine,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectBrowseDetailResponse response = projectBrowseService.getProjectDetail(projectId, page, size, mine, userDetails);
        return apiResponseFactory.success("프로젝트 상세 조회에 성공했습니다.", response, null);
    }
}
