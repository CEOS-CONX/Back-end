package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.dto.request.ProjectQuestionAnswerRequest;
import com.conx.server.project.dto.request.ProjectQuestionCreateRequest;
import com.conx.server.project.dto.response.ProjectQuestionDetailResponse;
import com.conx.server.project.dto.response.ProjectQuestionResponse;
import com.conx.server.project.service.ProjectQuestionService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "프로젝트 질문 상세 조회",
            description = "로그인 사용자가 모집 중인 프로젝트의 질문과 답변 상세를 조회합니다. 비밀 질문은 작성자, 해당 프로젝트를 등록한 기업, 관리자만 조회할 수 있습니다."
    )
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

    @Operation(
            summary = "프로젝트 질문 작성",
            description = "CREW 또는 COMPANY가 모집 중인 프로젝트에 질문을 작성합니다. content는 공백일 수 없으며 secret=true로 등록한 질문은 작성자·프로젝트 기업·관리자만 상세 내용을 볼 수 있습니다."
    )
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

    @Operation(
            summary = "프로젝트 질문 답변 등록",
            description = "프로젝트를 등록한 COMPANY 또는 ADMIN이 모집 중인 프로젝트의 질문에 답변합니다. answerContent는 공백일 수 없으며, 이미 답변된 질문에 다시 요청하면 기존 답변과 답변 시각을 덮어씁니다."
    )
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
