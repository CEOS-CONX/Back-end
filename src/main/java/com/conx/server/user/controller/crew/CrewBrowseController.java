package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewBrowseSort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.service.browse.CrewBrowseService;
import lombok.RequiredArgsConstructor;
import com.conx.server.user.service.browse.CrewBrowseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping
    public ApiResponse<Page<CrewBrowseResponse>> getCrews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) CrewType crewType,
            @RequestParam(required = false) CrewBrowseSort sort,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Page<CrewBrowseResponse> response = crewBrowseService.getCrews(
                keyword,
                category,
                crewType,
                sort,
                page,
                size
        );

        return apiResponseFactory.success("크루 목록 조회에 성공했습니다.", response, null);
    }

    @GetMapping("/{crewId}")
    public ApiResponse<CrewBrowseDetailResponse> getCrewDetail(
            @PathVariable Long crewId
    ) {
        CrewBrowseDetailResponse response = crewBrowseService.getCrewDetail(crewId);

        return apiResponseFactory.success("크루 상세 조회에 성공했습니다.", response, null);
    }
}