package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.company.request.CompanyEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationConfirmRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationSendRequest;
import com.conx.server.user.dto.company.request.CompanyJobUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyNameUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyPasswordUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyProfileUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyRepresentativeEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyRepresentativePhoneUpdateRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import com.conx.server.user.dto.company.response.CompanyCrewBookmarkToggleResponse;
import com.conx.server.user.dto.company.response.CompanyEmailVerificationConfirmResponse;
import com.conx.server.user.dto.company.response.CompanyProfileResponse;
import com.conx.server.user.service.mypage.CompanyMyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies/me")
@RequiredArgsConstructor
public class CompanyMyPageController {

    private final CompanyMyPageService companyMyPageService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping("/profile")
    public ApiResponse<CompanyProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CompanyProfileResponse response =
                companyMyPageService.getProfile(
                        userDetails.getId()
                );

        return apiResponseFactory.success(
                "기업 프로필 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/profile")
    public ApiResponse<CompanyProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyProfileUpdateRequest request
    ) {
        CompanyProfileResponse response =
                companyMyPageService.updateProfile(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "기업 프로필 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @GetMapping("/account")
    public ApiResponse<CompanyAccountResponse> getAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CompanyAccountResponse response =
                companyMyPageService.getAccount(
                        userDetails.getId()
                );

        return apiResponseFactory.success(
                "기업 계정 정보 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/account/name")
    public ApiResponse<CompanyAccountResponse> updateName(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyNameUpdateRequest request
    ) {
        CompanyAccountResponse response =
                companyMyPageService.updateName(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "이름 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/account/job")
    public ApiResponse<CompanyAccountResponse> updateJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyJobUpdateRequest request
    ) {
        CompanyAccountResponse response =
                companyMyPageService.updateJob(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "직무 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/account/representative-phone")
    public ApiResponse<CompanyAccountResponse>
    updateRepresentativePhone(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyRepresentativePhoneUpdateRequest request
    ) {
        CompanyAccountResponse response =
                companyMyPageService
                        .updateRepresentativePhone(
                                userDetails.getId(),
                                request
                        );

        return apiResponseFactory.success(
                "대표 전화번호 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/account/representative-email")
    public ApiResponse<CompanyAccountResponse>
    updateRepresentativeEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyRepresentativeEmailUpdateRequest request
    ) {
        CompanyAccountResponse response =
                companyMyPageService
                        .updateRepresentativeEmail(
                                userDetails.getId(),
                                request
                        );

        return apiResponseFactory.success(
                "대표 이메일 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/account/password")
    public ApiResponse<CompanyAccountResponse> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CompanyPasswordUpdateRequest request
    ) {
        CompanyAccountResponse response =
                companyMyPageService.updatePassword(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "비밀번호 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PostMapping("/account/email/verifications")
    public ApiResponse<Void> sendEmailChangeVerification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid
            @RequestBody
            CompanyEmailVerificationSendRequest request
    ) {
        companyMyPageService.sendEmailChangeVerification(
                userDetails.getId(),
                request
        );

        return apiResponseFactory.success(
                "새 이메일로 인증번호를 발송했습니다.",
                null,
                userDetails
        );
    }

    @PostMapping("/account/email/verifications/confirm")
    public ApiResponse<CompanyEmailVerificationConfirmResponse>
    confirmEmailChangeVerification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid
            @RequestBody
            CompanyEmailVerificationConfirmRequest request
    ) {
        CompanyEmailVerificationConfirmResponse response =
                companyMyPageService
                        .confirmEmailChangeVerification(
                                userDetails.getId(),
                                request
                        );

        return apiResponseFactory.success(
                "새 이메일 인증에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping("/account/email")
    public ApiResponse<CompanyAccountResponse> updateEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid
            @RequestBody
            CompanyEmailUpdateRequest request
    ) {
        CompanyAccountResponse response =
                companyMyPageService.updateEmail(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "계정 이메일 변경에 성공했습니다. 다시 로그인해주세요.",
                response,
                userDetails
        );
    }

    @PatchMapping("/bookmarked-crews/{crewId}")
    public ApiResponse<CompanyCrewBookmarkToggleResponse>
    toggleCrewBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "crewId") Long crewId
    ) {
        CompanyCrewBookmarkToggleResponse response =
                companyMyPageService.toggleCrewBookmark(
                        userDetails.getId(),
                        crewId
                );

        return apiResponseFactory.success(
                "크루 북마크 상태가 변경되었습니다.",
                response,
                userDetails
        );
    }

    @GetMapping("/bookmarked-crews")
    public ApiResponse<List<CompanyBookmarkedCrewResponse>>
    getBookmarkedCrews(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CompanyBookmarkedCrewResponse> response =
                companyMyPageService.getBookmarkedCrews(
                        userDetails.getId()
                );

        return apiResponseFactory.success(
                "북마크한 크루 목록 조회에 성공했습니다.",
                response,
                userDetails
        );
    }
}