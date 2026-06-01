package com.conx.server.bookmark.controller;

import com.conx.server.bookmark.dto.response.ProjectBookmarkResponse;
import com.conx.server.bookmark.service.ProjectBookmarkService;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectBookmarkController {

    private final ProjectBookmarkService projectBookmarkService;

    @PostMapping("/api/v1/projects/{projectId}/bookmarks")
    public ApiResponse<ProjectBookmarkResponse> addBookmark(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectBookmarkResponse response = projectBookmarkService.addBookmark(
                projectId,
                userDetails.getId()
        );

        return ApiResponse.success("프로젝트 북마크 등록에 성공했습니다.", response);
    }

    @DeleteMapping("/api/v1/projects/{projectId}/bookmarks")
    public ApiResponse<ProjectBookmarkResponse> removeBookmark(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectBookmarkResponse response = projectBookmarkService.removeBookmark(
                projectId,
                userDetails.getId()
        );

        return ApiResponse.success("프로젝트 북마크 취소에 성공했습니다.", response);
    }
}