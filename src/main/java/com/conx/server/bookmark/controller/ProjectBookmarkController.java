package com.conx.server.bookmark.controller;

import com.conx.server.bookmark.dto.response.ProjectBookmarkResponse;
import com.conx.server.bookmark.service.ProjectBookmarkService;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectBookmarkController {

    private final ProjectBookmarkService projectBookmarkService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 크루가 프로젝트를 북마크합니다.
     * @param projectId 프로젝트id
     * @param userDetails 인증정보
     */
    @PostMapping("/{projectId}/bookmarks")
    public ApiResponse<ProjectBookmarkResponse> addBookmark(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectBookmarkResponse response = projectBookmarkService.addBookmark(
                projectId,
                userDetails.getId()
        );

        return apiResponseFactory.success("프로젝트 북마크 등록에 성공했습니다.", response, userDetails);
    }

    /**
     * 크루가 프로젝트 북마크를 취소합니다.
     * @param projectId 프로젝트id
     * @param userDetails 인증정보
     */
    @DeleteMapping("/{projectId}/bookmarks")
    public ApiResponse<ProjectBookmarkResponse> removeBookmark(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectBookmarkResponse response = projectBookmarkService.removeBookmark(
                projectId,
                userDetails.getId()
        );

        return apiResponseFactory.success("프로젝트 북마크 취소에 성공했습니다.", response, userDetails);
    }
}