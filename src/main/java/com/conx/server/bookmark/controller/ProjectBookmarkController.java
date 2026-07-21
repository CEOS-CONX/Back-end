package com.conx.server.bookmark.controller;

import com.conx.server.bookmark.dto.response.ProjectBookmarkResponse;
import com.conx.server.bookmark.service.ProjectBookmarkService;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "프로젝트 북마크 등록",
            description = "활성 CREW가 모집 중인 프로젝트를 북마크합니다. 동일 프로젝트를 중복 북마크할 수 없으며, 성공 응답의 bookmarked는 true입니다."
    )
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
    @Operation(
            summary = "프로젝트 북마크 해제",
            description = "CREW 본인의 프로젝트 북마크를 실제 삭제합니다. 존재하지 않는 북마크는 삭제할 수 없으며, 성공 응답의 bookmarked는 false입니다."
    )
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
