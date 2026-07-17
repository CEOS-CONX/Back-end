package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.service.mypage.CrewMyPageService;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.request.CrewRepresentativeProjectsUpdateRequest;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.dto.crew.response.CrewRepresentativeProjectCandidateResponse;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PatchMapping
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

    @PostMapping("/portfolio")
    public ApiResponse<CrewPortfolioResponseDTO> registerPortfolio(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CrewPortfolioRequestDTO req
    ) {
        CrewPortfolioResponseDTO response = crewMyPageService.registerPortfolio(
                customUserDetails.getId(), req
        );

        return apiResponseFactory.success("크루 포트폴리오 등록에 성공했습니다.", response, customUserDetails);
    }

    @PatchMapping("/portfolio/{portfolioId}")
    public ApiResponse<CrewPortfolioResponseDTO> modifyPortfolio(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long portfolioId,
            @RequestBody ModifyCrewPortfolioRequestDTO req
    ) {
        CrewPortfolioResponseDTO response = crewMyPageService.modifyPortfolio(
                customUserDetails.getId(), portfolioId, req
        );

        return apiResponseFactory.success("크루 포트폴리오 수정에 성공했습니다.", response, customUserDetails);
    }

    @DeleteMapping("/portfolio/{portfolioId}")
    public ApiResponse<?> deletePortfolio(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long portfolioId,
            @RequestBody ModifyCrewPortfolioRequestDTO req
    ) {
        crewMyPageService.deletePortfolio(customUserDetails.getId(), portfolioId);
        return apiResponseFactory.success("크루 포트폴리오 삭제에 성공했습니다.", customUserDetails);
    }

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