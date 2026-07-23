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
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "기업 프로필 조회",
            description = "로그인한 기업의 회사명, 브랜드명, 업종, 기업 소개와 프로필 관련 정보를 조회합니다. COMPANY 권한의 로그인이 필요합니다."
    )
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

    @Operation(
            summary = "기업 프로필 부분 수정",
            description = "로그인한 기업의 프로필을 부분 수정하며 null인 필드는 기존 값을 유지합니다. 수정할 필드만 전달할 수 있고, 빈 문자열은 실제 값으로 저장됩니다."
    )
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

    @Operation(
            summary = "기업 계정 정보 조회",
            description = "로그인한 기업의 담당자 이름, 로그인 이메일, 직무, 대표 전화번호와 대표 이메일을 조회합니다. 로그인 이메일과 대표 이메일은 서로 다른 정보입니다."
    )
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

    @Operation(
            summary = "기업 담당자 이름 수정",
            description = "현재 비밀번호를 확인한 후 담당자 이름을 변경합니다. currentPassword와 공백이 아닌 name을 모두 입력해야 합니다."
    )
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

    @Operation(
            summary = "기업 담당자 직무 수정",
            description = "현재 비밀번호를 확인한 후 담당자의 직무를 변경합니다. currentPassword와 공백이 아닌 job을 모두 입력해야 합니다."
    )
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

    @Operation(
            summary = "기업 대표 전화번호 수정",
            description = "현재 비밀번호를 확인한 후 기업의 대표 전화번호를 변경합니다. currentPassword와 공백이 아닌 representativePhone을 모두 입력해야 합니다."
    )
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

    @Operation(
            summary = "기업 대표 이메일 수정",
            description = "현재 비밀번호를 확인한 후 기업의 공용 대표 이메일을 변경합니다. 로그인 이메일 변경 API가 아니며, currentPassword와 representativeEmail을 모두 입력해야 합니다."
    )
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

    @Operation(
            summary = "기업 계정 비밀번호 변경",
            description = "현재 비밀번호를 확인하고 새 비밀번호와 확인값이 일치하면 비밀번호를 변경합니다. currentPassword, newPassword, newPasswordConfirmation을 모두 입력해야 하며 변경 후 재로그인이 필요할 수 있습니다."
    )
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

    @Operation(
            summary = "계정 이메일 변경 인증번호 발송",
            description = "현재 비밀번호를 확인한 후 사용 가능한 새 로그인 이메일로 6자리 인증번호를 발송하며 인증번호는 5분간 유효합니다. 이메일 변경 절차의 첫 번째 단계입니다."
    )
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

    @Operation(
            summary = "계정 이메일 변경 인증번호 확인",
            description = "첫 단계에서 입력한 새 이메일과 수신한 인증번호를 확인하고 30분간 유효한 이메일 변경용 verificationToken을 발급합니다. 발급된 토큰은 최종 이메일 변경 요청에 사용해야 합니다."
    )
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

    @Operation(
            summary = "기업 계정 로그인 이메일 변경",
            description = "이메일 인증 확인 단계에서 발급받은 verificationToken, 동일한 newEmail, 현재 비밀번호로 로그인 이메일을 최종 변경합니다. 인증 토큰은 30분 내 한 번만 사용할 수 있으며 변경 후 새 이메일로 다시 로그인해야 합니다."
    )
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

    @Operation(
            summary = "크루 북마크 등록·해제",
            description = "crewId에 해당하는 활성 크루의 북마크 상태를 토글합니다. 기존 북마크가 있으면 해제하고 없으면 등록하며, 응답의 bookmarked로 변경 결과를 확인할 수 있습니다."
    )
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

    @Operation(
            summary = "북마크한 크루 목록 조회",
            description = "로그인한 기업이 북마크한 크루 전체와 평균 평점을 조회합니다. 페이지네이션과 보장된 정렬 기준은 없습니다."
    )
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
