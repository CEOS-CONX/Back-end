package com.conx.server.user.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.company.request.CompanyAccountUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyProfileUpdateRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyProfileResponse;
import com.conx.server.user.service.mypage.CompanyMyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import com.conx.server.user.dto.company.response.CompanyCrewBookmarkToggleResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies/me")
@RequiredArgsConstructor
public class CompanyMyPageController {

    private final CompanyMyPageService companyMyPageService;

    @GetMapping("/profile")
    public ApiResponse<CompanyProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CompanyProfileResponse response = companyMyPageService.getProfile(userDetails.getId());
        return ApiResponse.success("기업 프로필 조회에 성공했습니다.", response);
    }

    @PatchMapping("/profile")
    public ApiResponse<CompanyProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyProfileUpdateRequest request
    ) {
        CompanyProfileResponse response = companyMyPageService.updateProfile(userDetails.getId(), request);
        return ApiResponse.success("기업 프로필 수정에 성공했습니다.", response);
    }

    @GetMapping("/account")
    public ApiResponse<CompanyAccountResponse> getAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CompanyAccountResponse response = companyMyPageService.getAccount(userDetails.getId());
        return ApiResponse.success("기업 계정 정보 조회에 성공했습니다.", response);
    }

    @PatchMapping("/account")
    public ApiResponse<CompanyAccountResponse> updateAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyAccountUpdateRequest request
    ) {
        CompanyAccountResponse response = companyMyPageService.updateAccount(userDetails.getId(), request);
        return ApiResponse.success("기업 계정 정보 수정에 성공했습니다.", response);
    }

    @PatchMapping("/bookmarked-crews/{crewId}")
    public ApiResponse<CompanyCrewBookmarkToggleResponse> toggleCrewBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long crewId
    ) {
        CompanyCrewBookmarkToggleResponse response =
                companyMyPageService.toggleCrewBookmark(userDetails.getId(), crewId);

        return ApiResponse.success("크루 북마크 상태가 변경되었습니다.", response);
    }

    @GetMapping("/bookmarked-crews")
    public ApiResponse<List<CompanyBookmarkedCrewResponse>> getBookmarkedCrews(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CompanyBookmarkedCrewResponse> response =
                companyMyPageService.getBookmarkedCrews(userDetails.getId());

        return ApiResponse.success("북마크한 크루 목록 조회에 성공했습니다.", response);
    }
}