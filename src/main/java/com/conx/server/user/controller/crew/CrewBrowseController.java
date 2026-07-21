package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewBrowseSort;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.service.browse.CrewBrowseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
public class CrewBrowseController {

    private final CrewBrowseService crewBrowseService;
    private final ApiResponseFactory apiResponseFactory;

    @Operation(
            summary = "크루 목록 조회",
            description = "활성 크루를 검색·필터링하여 페이지 단위로 조회합니다. keyword는 크루명·소개를 검색하며 category는 BEAUTY, FASHION, IT, CAREER, FANDB, LIFESTYLE, ENTERTAIN, ETC, crewType은 ACADEMY, SMALLMEETING, CLUB, COUNCIL, ETC, sort는 RECENT(기본값), POPULAR, RATING, RECOMMENDED를 사용할 수 있습니다."
    )
    @GetMapping
    public ApiResponse<Page<CrewBrowseResponse>> getCrews(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Industry category,

            @RequestParam(required = false)
            CrewType crewType,

            @RequestParam(required = false)
            CrewBrowseSort sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "12")
            int size,

            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        Page<CrewBrowseResponse> response =
                crewBrowseService.getCrews(
                        keyword,
                        category,
                        crewType,
                        sort,
                        page,
                        size,
                        userDetails
                );

        return apiResponseFactory.success(
                "크루 목록 조회에 성공했습니다.",
                response,
                null
        );
    }

    @Operation(
            summary = "크루 상세 조회",
            description = "로그인 사용자가 활성 크루의 공개 프로필과 대표 프로젝트를 조회합니다. 공개 상세 정보가 없는 크루는 hasPublicDetail이 false이며 상세 프로필 관련 값이 null로 반환됩니다."
    )
    @GetMapping("/{crewId}")
    public ApiResponse<CrewBrowseDetailResponse>
    getCrewDetail(
            @PathVariable
            Long crewId,

            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        CrewBrowseDetailResponse response =
                crewBrowseService.getCrewDetail(
                        crewId,
                        userDetails
                );

        return apiResponseFactory.success(
                "크루 상세 조회에 성공했습니다.",
                response,
                null
        );
    }

    @Operation(
            summary = "크루 프로젝트 이력 조회",
            description = "로그인 사용자가 크루의 수행 프로젝트를 페이지 단위로 조회합니다. sort는 RECENT(기본값) 또는 OLDEST이며 size는 1~8 범위로 보정됩니다."
    )
    @GetMapping("/{crewId}/projects")
    public ApiResponse<Page<CrewProjectHistoryResponse>>
    getCrewProjects(
            @PathVariable
            Long crewId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "8")
            int size,

            @RequestParam(
                    defaultValue = "RECENT"
            )
            CrewProjectHistorySort sort
    ) {
        Page<CrewProjectHistoryResponse> response =
                crewBrowseService.getCrewProjects(
                        crewId,
                        page,
                        size,
                        sort
                );

        return apiResponseFactory.success(
                "크루 프로젝트 이력 조회에 성공했습니다.",
                response,
                null
        );
    }
}
