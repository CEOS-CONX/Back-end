package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewBrowseSort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.service.browse.CrewBrowseService;import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
public class CrewBrowseController {

    private final CrewBrowseService crewBrowseService;

    @GetMapping
    public ApiResponse<List<CrewBrowseResponse>> getCrews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Industry category,
            @RequestParam(required = false) CrewType crewType,
            @RequestParam(required = false) CrewBrowseSort sort
    ) {
        List<CrewBrowseResponse> response = crewBrowseService.getCrews(
                keyword,
                category,
                crewType,
                sort
        );

        return ApiResponse.success("크루 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/{crewId}")
    public ApiResponse<CrewBrowseDetailResponse> getCrewDetail(
            @PathVariable Long crewId
    ) {
        CrewBrowseDetailResponse response = crewBrowseService.getCrewDetail(crewId);

        return ApiResponse.success("크루 상세 조회에 성공했습니다.", response);
    }
}