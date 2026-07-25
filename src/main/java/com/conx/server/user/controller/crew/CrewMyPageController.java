package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.request.CrewEmailUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewEmailVerificationConfirmRequest;
import com.conx.server.user.dto.crew.request.CrewEmailVerificationSendRequest;
import com.conx.server.user.dto.crew.request.CrewNameUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewPasswordUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewRepresentativeEmailUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewRepresentativePhoneUpdateRequest;
import com.conx.server.user.dto.crew.request.CrewRepresentativeProjectsUpdateRequest;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.response.CrewAccountResponse;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewEmailVerificationConfirmResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.dto.crew.response.CrewRepresentativeProjectCandidateResponse;
import com.conx.server.user.service.mypage.CrewMyPageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews/me")
public class CrewMyPageController {

    private final CrewMyPageService crewMyPageService;
    private final ApiResponseFactory apiResponseFactory;

    @Operation(
            summary = "내 크루 프로필 조회",
            description = "로그인한 크루가 자신의 프로필, 링크, 소개 파일, 포트폴리오와 대표 프로젝트를 조회합니다. 기존 /api/v1/crews/me 경로도 호환을 위해 지원합니다."
    )
    @GetMapping({"", "/profile"})
    public ApiResponse<CrewProfileResponse> getProfile(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        CrewProfileResponse response =
                crewMyPageService.getProfile(
                        userDetails.getId()
                );

        return apiResponseFactory.success(
                "크루 프로필 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 프로필 수정",
            description = "로그인한 크루의 프로필을 부분 수정합니다. 단일 값과 목록이 null이면 기존 값을 유지하고, schools·advantages·specialties·links·files에 빈 배열을 보내면 해당 목록을 모두 삭제합니다. 기존 /api/v1/crews/me 경로도 호환을 위해 지원합니다."
    )
    @PatchMapping({"", "/profile"})
    public ApiResponse<CrewProfileResponse> updateProfile(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewProfileUpdateRequest request
    ) {
        CrewProfileResponse response =
                crewMyPageService.updateProfile(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 프로필 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 계정 정보 조회",
            description = "로그인한 크루의 이름, 계정 이메일, 대표 전화번호와 대표 이메일을 조회합니다. 비밀번호는 응답에 포함하지 않습니다."
    )
    @GetMapping("/account")
    public ApiResponse<CrewAccountResponse> getAccount(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        CrewAccountResponse response =
                crewMyPageService.getAccount(
                        userDetails.getId()
                );

        return apiResponseFactory.success(
                "크루 계정 정보 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 이름 수정",
            description = "로그인한 크루의 담당자 이름을 수정합니다. 현재 비밀번호 확인은 필요하지 않습니다."
    )
    @PatchMapping("/account/name")
    public ApiResponse<CrewAccountResponse> updateName(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewNameUpdateRequest request
    ) {
        CrewAccountResponse response =
                crewMyPageService.updateName(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 이름 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 대표 전화번호 수정",
            description = "로그인한 크루의 대표 전화번호를 수정합니다. 현재 비밀번호 확인은 필요하지 않습니다."
    )
    @PatchMapping("/account/representative-phone")
    public ApiResponse<CrewAccountResponse> updateRepresentativePhone(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewRepresentativePhoneUpdateRequest request
    ) {
        CrewAccountResponse response =
                crewMyPageService.updateRepresentativePhone(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 대표 전화번호 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 대표 이메일 수정",
            description = "로그인한 크루의 대표 이메일을 수정합니다. 로그인에 사용하는 계정 이메일은 변경되지 않으며 현재 비밀번호 확인은 필요하지 않습니다."
    )
    @PatchMapping("/account/representative-email")
    public ApiResponse<CrewAccountResponse> updateRepresentativeEmail(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewRepresentativeEmailUpdateRequest request
    ) {
        CrewAccountResponse response =
                crewMyPageService.updateRepresentativeEmail(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 대표 이메일 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 비밀번호 수정",
            description = "현재 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다. 새 비밀번호와 확인 값이 일치해야 하며, 변경 후 기존 Refresh Token을 삭제합니다."
    )
    @PatchMapping("/account/password")
    public ApiResponse<CrewAccountResponse> updatePassword(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewPasswordUpdateRequest request
    ) {
        CrewAccountResponse response =
                crewMyPageService.updatePassword(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 비밀번호 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 계정 이메일 변경 인증번호 발송",
            description = "현재 비밀번호를 확인한 뒤 새 계정 이메일로 6자리 인증번호를 발송합니다. 인증번호는 5분간 유효합니다."
    )
    @PostMapping("/account/email/verifications")
    public ApiResponse<?> sendEmailChangeVerification(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewEmailVerificationSendRequest request
    ) {
        crewMyPageService.sendEmailChangeVerification(
                userDetails.getId(),
                request
        );

        return apiResponseFactory.success(
                "크루 계정 이메일 변경 인증번호 발송에 성공했습니다.",
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 계정 이메일 변경 인증번호 확인",
            description = "새 계정 이메일로 발송된 인증번호를 확인하고 최종 이메일 변경에 사용할 일회성 인증 토큰을 발급합니다."
    )
    @PostMapping("/account/email/verifications/confirm")
    public ApiResponse<CrewEmailVerificationConfirmResponse>
    confirmEmailChangeVerification(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewEmailVerificationConfirmRequest request
    ) {
        CrewEmailVerificationConfirmResponse response =
                crewMyPageService
                        .confirmEmailChangeVerification(
                                userDetails.getId(),
                                request
                        );

        return apiResponseFactory.success(
                "크루 계정 이메일 변경 인증에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "내 크루 계정 이메일 수정",
            description = "현재 비밀번호와 이메일 인증 토큰을 확인한 뒤 로그인에 사용하는 계정 이메일을 변경합니다. 변경 후 기존 Refresh Token을 삭제합니다."
    )
    @PatchMapping("/account/email")
    public ApiResponse<CrewAccountResponse> updateEmail(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewEmailUpdateRequest request
    ) {
        CrewAccountResponse response =
                crewMyPageService.updateEmail(
                        userDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 계정 이메일 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "대표 프로젝트 후보 조회",
            description = "로그인한 크루의 수행 프로젝트 중 대표 프로젝트로 선택 가능한 후보와 현재 선택 여부를 조회합니다. sort는 RECENT(기본값) 또는 OLDEST이며 size는 1~20 범위로 보정됩니다."
    )
    @GetMapping("/representative-project-candidates")
    public ApiResponse<Page<CrewRepresentativeProjectCandidateResponse>> getRepresentativeProjectCandidates(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "RECENT")
            CrewProjectHistorySort sort
    ) {
        Page<CrewRepresentativeProjectCandidateResponse>
                response =
                crewMyPageService
                        .getRepresentativeProjectCandidates(
                                userDetails.getId(),
                                page,
                                size,
                                sort
                        );

        return apiResponseFactory.success(
                "대표 프로젝트 후보 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "대표 프로젝트 수정",
            description = "로그인한 크루의 대표 프로젝트를 projectIds 순서대로 최대 3개까지 전체 교체합니다. 본인이 수행한 지정 상태의 프로젝트만 선택할 수 있으며 빈 배열을 보내면 모두 해제됩니다."
    )
    @PatchMapping("/representative-projects")
    public ApiResponse<List<CrewProjectHistoryResponse>>
    updateRepresentativeProjects(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            CrewRepresentativeProjectsUpdateRequest request
    ) {
        List<CrewProjectHistoryResponse> response =
                crewMyPageService
                        .updateRepresentativeProjects(
                                userDetails.getId(),
                                request
                        );

        return apiResponseFactory.success(
                "대표 프로젝트 수정에 성공했습니다.",
                response,
                userDetails
        );
    }

    @Operation(
            summary = "크루 포트폴리오 등록",
            description = "로그인한 크루의 포트폴리오를 등록합니다. 요청 본문에 name과 fileLink를 입력하고 imageLink는 선택적으로 전달합니다."
    )
    @PostMapping("/portfolio")
    public ApiResponse<CrewPortfolioResponseDTO> registerPortfolio(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,
            @Valid
            @RequestBody
            CrewPortfolioRequestDTO request
    ) {
        CrewPortfolioResponseDTO response =
                crewMyPageService.registerPortfolio(
                        customUserDetails.getId(),
                        request
                );

        return apiResponseFactory.success(
                "크루 포트폴리오 등록에 성공했습니다.",
                response,
                customUserDetails
        );
    }

    @Operation(
            summary = "크루 포트폴리오 수정",
            description = "로그인한 크루가 자신이 소유한 포트폴리오의 imageLink, name, fileLink를 부분 수정합니다. null로 전달한 필드는 기존 값을 유지합니다."
    )
    @PatchMapping("/portfolio/{portfolioId}")
    public ApiResponse<CrewPortfolioResponseDTO> modifyPortfolio(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @PathVariable
            Long portfolioId,

            @RequestBody
            ModifyCrewPortfolioRequestDTO request
    ) {
        CrewPortfolioResponseDTO response =
                crewMyPageService.modifyPortfolio(
                        customUserDetails.getId(),
                        portfolioId,
                        request
                );

        return apiResponseFactory.success(
                "크루 포트폴리오 수정에 성공했습니다.",
                response,
                customUserDetails
        );
    }

    @Operation(
            summary = "크루 포트폴리오 삭제",
            description = "로그인한 크루가 자신이 소유한 포트폴리오를 삭제합니다. 삭제할 포트폴리오 ID를 경로 변수로 전달합니다."
    )
    @DeleteMapping("/portfolio/{portfolioId}")
    public ApiResponse<?> deletePortfolio(
            @AuthenticationPrincipal
            CustomUserDetails customUserDetails,

            @PathVariable
            Long portfolioId
    ) {
        crewMyPageService.deletePortfolio(
                customUserDetails.getId(),
                portfolioId
        );

        return apiResponseFactory.success(
                "크루 포트폴리오 삭제에 성공했습니다.",
                customUserDetails
        );
    }

    @Operation(
            summary = "북마크한 프로젝트 목록 조회",
            description = "로그인한 크루가 북마크한 프로젝트를 페이지 단위로 조회합니다. page 기본값은 0, size 기본값은 10이며 별도의 필터와 정렬 조건은 없습니다."
    )
    @GetMapping("/bookmarked-projects")
    public ApiResponse<Page<CrewBookmarkedProjectResponse>>
    getBookmarkedProjects(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        Page<CrewBookmarkedProjectResponse> response =
                crewMyPageService.getBookmarkedProjects(
                        userDetails.getId(),
                        PageRequest.of(
                                Math.max(page, 0),
                                Math.max(size, 1)
                        )
                );

        return apiResponseFactory.success(
                "북마크한 프로젝트 목록 조회에 성공했습니다.",
                response,
                userDetails
        );
    }
}
