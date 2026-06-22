package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.service.mypage.CrewMyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews/me")
public class CrewMyPageController {

    private final CrewMyPageService crewMyPageService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping
    public ApiResponse<CrewProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CrewProfileResponse response = crewMyPageService.getProfile(userDetails.getId());
        return apiResponseFactory.success("크루 프로필 조회에 성공했습니다.", response, userDetails);
    }

    @PatchMapping
    public ApiResponse<CrewProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CrewProfileUpdateRequest request
    ) {
        CrewProfileResponse response = crewMyPageService.updateProfile(
                userDetails.getId(),
                request
        );

        return apiResponseFactory.success("크루 프로필 수정에 성공했습니다.", response, userDetails);
    }

    @GetMapping("/bookmarked-projects")
    public ApiResponse<Page<CrewBookmarkedProjectResponse>> getBookmarkedProjects(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CrewBookmarkedProjectResponse> response =
                crewMyPageService.getBookmarkedProjects(
                        userDetails.getId(),
                        PageRequest.of(page, size)
                );

        return apiResponseFactory.success("북마크한 프로젝트 목록 조회에 성공했습니다.", response, userDetails);
    }
}