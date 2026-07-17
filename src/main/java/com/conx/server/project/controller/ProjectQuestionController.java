package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.dto.request.ProjectQuestionAnswerRequest;
import com.conx.server.project.dto.request.ProjectQuestionCreateRequest;
import com.conx.server.project.dto.response.ProjectQuestionDetailResponse;
import com.conx.server.project.dto.response.ProjectQuestionResponse;
import com.conx.server.project.service.ProjectQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectQuestionController {

    private final ProjectQuestionService projectQuestionService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping("/api/v1/projects/{projectId}/questions")
    public ApiResponse<Page<ProjectQuestionResponse>> getQuestions(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean mine,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Page<ProjectQuestionResponse> response =
                projectQuestionService.getQuestions(
                        projectId,
                        page,
                        size,
                        mine,
                        userDetails
                );

        return apiResponseFactory.success(
                "프로젝트 질문 목록 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @GetMapping("/api/v1/projects/{projectId}/questions/{questionId}")
    public ApiResponse<ProjectQuestionDetailResponse> getQuestion(
            @PathVariable Long projectId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectQuestionDetailResponse response =
                projectQuestionService.getQuestion(
                        projectId,
                        questionId,
                        userDetails
                );

        return apiResponseFactory.success(
                "프로젝트 질문 상세 조회에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PostMapping("/api/v1/projects/{projectId}/questions")
    public ApiResponse<ProjectQuestionDetailResponse> createQuestion(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProjectQuestionCreateRequest request
    ) {
        ProjectQuestionDetailResponse response =
                projectQuestionService.createQuestion(
                        projectId,
                        userDetails,
                        request
                );

        return apiResponseFactory.success(
                "프로젝트 질문 작성에 성공했습니다.",
                response,
                userDetails
        );
    }

    @PatchMapping(
            "/api/v1/projects/{projectId}/questions/{questionId}/answer"
    )
    public ApiResponse<ProjectQuestionDetailResponse> answerQuestion(
            @PathVariable Long projectId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProjectQuestionAnswerRequest request
    ) {
        ProjectQuestionDetailResponse response =
                projectQuestionService.answerQuestion(
                        projectId,
                        questionId,
                        userDetails,
                        request
                );

        return apiResponseFactory.success(
                "프로젝트 질문 답변 저장에 성공했습니다.",
                response,
                userDetails
        );
    }
}