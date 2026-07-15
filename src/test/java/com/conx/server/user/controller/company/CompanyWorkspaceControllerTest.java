package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.dto.company.request.CompanyProjectRequest;
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.user.dto.company.request.CompanySettlementCompleteRequest;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.CompanyPartnerCrewResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationDetailResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationSelectResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApprovalResponse;
import com.conx.server.user.dto.company.response.CompanyProjectDraftResponse;
import com.conx.server.user.dto.company.response.CompanyProjectIdResponse;
import com.conx.server.user.dto.company.response.CompanyProjectRevisionResponse;
import com.conx.server.user.dto.company.response.CompanySettlementCompleteResponse;
import com.conx.server.user.dto.company.response.CompanySettlementExpectedPaymentDateResponse;
import com.conx.server.user.dto.company.response.CompanySettlementResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceDashboardResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectResponse;
import com.conx.server.user.service.workspace.CompanyWorkspaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.user.dto.crew.CrewSubmissionReplyStatus;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDetailResponse;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionListItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyWorkspaceControllerTest {

    private static final Long COMPANY_ID = 1L;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CompanyWorkspaceService companyWorkspaceService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory = new ApiResponseFactory(notificationRepository);

        CompanyWorkspaceController companyWorkspaceController = new CompanyWorkspaceController(
                companyWorkspaceService,
                apiResponseFactory
        );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        lenient()
                .when(userDetails.getId())
                .thenReturn(COMPANY_ID);

        lenient()
                .when(
                        notificationRepository.existsByreceiverIdAndIsRead(
                                COMPANY_ID,
                                false
                        )
                )
                .thenReturn(false);

        mockMvc = MockMvcBuilders
                .standaloneSetup(companyWorkspaceController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(
                            MethodParameter parameter,
                            ModelAndViewContainer mavContainer,
                            NativeWebRequest webRequest,
                            WebDataBinderFactory binderFactory
                    ) {
                        return userDetails;
                    }
                })
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("기업 워크스페이스 대시보드를 조회한다")
    void getDashboard() throws Exception {
        CompanyWorkspaceDashboardResponse response =
                new CompanyWorkspaceDashboardResponse(10, 3);

        given(companyWorkspaceService.getDashboard(COMPANY_ID)).willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/workspace/dashboard"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 워크스페이스 대시보드 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.totalProjectCount").value(10))
                .andExpect(jsonPath("$.payload.recruitingProjectCount").value(3))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getDashboard(COMPANY_ID);
    }

    @Test
    @DisplayName("기업 프로젝트 목록을 조회한다")
    void getProjects() throws Exception {
        CompanyWorkspaceProjectResponse project = createProjectResponse(100L);

        given(companyWorkspaceService.getProjects(
                eq(COMPANY_ID),
                eq("브랜드"),
                eq(null),
                eq(LocalDate.of(2026, 6, 1)),
                eq(LocalDate.of(2026, 6, 30))
        )).willReturn(List.of(project));

        mockMvc.perform(get("/api/v1/companies/me/projects")
                        .param("keyword", "브랜드")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-06-30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 프로젝트 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].projectId").value(100))
                .andExpect(jsonPath("$.payload[0].name").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.payload[0].brandName").value("테스트 브랜드"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjects(
                COMPANY_ID,
                "브랜드",
                null,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );
    }

    @Test
    @DisplayName("기업 프로젝트 상세를 조회한다")
    void getProjectDetail() throws Exception {
        Long projectId = 100L;
        CompanyWorkspaceProjectDetailResponse response = createProjectDetailResponse(projectId);

        given(companyWorkspaceService.getProjectDetail(COMPANY_ID, projectId))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 프로젝트 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.name").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.payload.brandName").value("테스트 브랜드"))
                .andExpect(jsonPath("$.payload.selectedCrewId").value(10))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjectDetail(COMPANY_ID, projectId);
    }

    @Test
    @DisplayName("새 프로젝트를 등록한다")
    void createProject() throws Exception {
        CompanyProjectRequest request = createProjectRequest();
        CompanyProjectIdResponse response = new CompanyProjectIdResponse(100L, null);

        given(companyWorkspaceService.createProject(eq(COMPANY_ID), eq(request)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/companies/me/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("새 프로젝트 등록에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).createProject(eq(COMPANY_ID), eq(request));
    }

    @Test
    @DisplayName("프로젝트를 수정한다")
    void updateProject() throws Exception {
        Long projectId = 100L;
        CompanyProjectRequest request = createProjectRequest();
        CompanyProjectIdResponse response = new CompanyProjectIdResponse(projectId, null);

        given(companyWorkspaceService.updateProject(eq(COMPANY_ID), eq(projectId), eq(request)))
                .willReturn(response);

        mockMvc.perform(patch("/api/v1/companies/me/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).updateProject(eq(COMPANY_ID), eq(projectId), eq(request));
    }

    @Test
    @DisplayName("프로젝트를 삭제한다")
    void deleteProject() throws Exception {
        Long projectId = 100L;

        mockMvc.perform(delete("/api/v1/companies/me/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 삭제에 성공했습니다."))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).deleteProject(COMPANY_ID, projectId);
    }

    @Test
    @DisplayName("프로젝트를 임시저장한다")
    void createProjectDraft() throws Exception {
        CompanyProjectRequest request = createProjectRequest();
        CompanyProjectIdResponse response = new CompanyProjectIdResponse(200L, null);

        given(companyWorkspaceService.createProjectDraft(eq(COMPANY_ID), eq(request)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/companies/me/project-drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 임시저장에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(200))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).createProjectDraft(eq(COMPANY_ID), eq(request));
    }

    @Test
    @DisplayName("임시저장 프로젝트를 수정한다")
    void updateProjectDraft() throws Exception {
        Long draftId = 200L;
        CompanyProjectRequest request = createProjectRequest();
        CompanyProjectIdResponse response = new CompanyProjectIdResponse(draftId, null);

        given(companyWorkspaceService.updateProjectDraft(eq(COMPANY_ID), eq(draftId), eq(request)))
                .willReturn(response);

        mockMvc.perform(patch("/api/v1/companies/me/project-drafts/{draftId}", draftId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("임시저장 프로젝트 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(200))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).updateProjectDraft(eq(COMPANY_ID), eq(draftId), eq(request));
    }

    @Test
    @DisplayName("임시저장 프로젝트를 조회한다")
    void getProjectDraft() throws Exception {
        Long draftId = 200L;
        CompanyProjectDraftResponse response = createProjectDraftResponse(draftId);

        given(companyWorkspaceService.getProjectDraft(COMPANY_ID, draftId))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/project-drafts/{draftId}", draftId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("임시저장 프로젝트 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(200))
                .andExpect(jsonPath("$.payload.name").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjectDraft(COMPANY_ID, draftId);
    }

    @Test
    @DisplayName("검수할 프로젝트 상세를 조회한다")
    void getProjectReviewDetail() throws Exception {
        Long projectId = 100L;
        CompanyWorkspaceProjectDetailResponse response = createProjectDetailResponse(projectId);

        given(companyWorkspaceService.getProjectReviewDetail(COMPANY_ID, projectId))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/projects/{projectId}/review", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("검수할 프로젝트 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.name").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjectReviewDetail(COMPANY_ID, projectId);
    }

    @Test
    @DisplayName("기업이 프로젝트 결과물 공유 이력을 조회한다")
    void getProjectSubmissions() throws Exception {
        Long projectId = 100L;

        CrewProjectSubmissionListItemResponse submission =
                new CrewProjectSubmissionListItemResponse(
                        400L,
                        "1차 결과물",
                        "테스트 크루",
                        LocalDateTime.of(
                                2026,
                                7,
                                15,
                                14,
                                30
                        ),
                        ProjectSubmissionStatus.SUBMITTED,
                        CrewSubmissionReplyStatus.WAITING_FOR_REPLY
                );

        Page<CrewProjectSubmissionListItemResponse> response =
                new PageImpl<>(
                        List.of(submission),
                        PageRequest.of(0, 10),
                        1
                );

        given(
                companyWorkspaceService
                        .getProjectSubmissions(
                                COMPANY_ID,
                                projectId,
                                0,
                                10
                        )
        ).willReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/companies/me/projects/{projectId}/submissions",
                                projectId
                        )
                                .param("page", "0")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 결과물 공유 이력 조회에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.content[0].submissionId")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.payload.content[0].title")
                                .value("1차 결과물")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].authorName")
                                .value("테스트 크루")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].submissionStatus")
                                .value("SUBMITTED")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].replyStatus")
                                .value("WAITING_FOR_REPLY")
                )
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.hasNotification")
                                .value(false)
                );

        verify(companyWorkspaceService)
                .getProjectSubmissions(
                        COMPANY_ID,
                        projectId,
                        0,
                        10
                );
    }

    @Test
    @DisplayName("기업이 프로젝트 결과물 상세를 조회한다")
    void getProjectSubmissionDetail() throws Exception {
        Long projectId = 100L;
        Long submissionId = 400L;

        CrewProjectSubmissionDetailResponse response =
                new CrewProjectSubmissionDetailResponse(
                        submissionId,
                        projectId,
                        "1차 결과물",
                        "테스트 크루",
                        LocalDateTime.of(
                                2026,
                                7,
                                15,
                                14,
                                30
                        ),
                        ProjectSubmissionStatus.SUBMITTED,
                        CrewSubmissionReplyStatus.WAITING_FOR_REPLY,
                        "결과물 설명입니다.",
                        List.of(
                                "https://example.com/result.pdf"
                        ),
                        List.of(
                                "https://example.com/reference"
                        ),
                        null
                );

        given(
                companyWorkspaceService
                        .getProjectSubmissionDetail(
                                COMPANY_ID,
                                projectId,
                                submissionId
                        )
        ).willReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/companies/me/projects/{projectId}/submissions/{submissionId}",
                                projectId,
                                submissionId
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 결과물 상세 조회에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.submissionId")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.payload.title")
                                .value("1차 결과물")
                )
                .andExpect(
                        jsonPath("$.payload.content")
                                .value("결과물 설명입니다.")
                )
                .andExpect(
                        jsonPath("$.payload.fileLinks[0]")
                                .value(
                                        "https://example.com/result.pdf"
                                )
                )
                .andExpect(
                        jsonPath("$.payload.referenceLinks[0]")
                                .value(
                                        "https://example.com/reference"
                                )
                )
                .andExpect(
                        jsonPath("$.payload.submissionStatus")
                                .value("SUBMITTED")
                )
                .andExpect(
                        jsonPath("$.payload.replyStatus")
                                .value("WAITING_FOR_REPLY")
                )
                .andExpect(
                        jsonPath("$.hasNotification")
                                .value(false)
                );

        verify(companyWorkspaceService)
                .getProjectSubmissionDetail(
                        COMPANY_ID,
                        projectId,
                        submissionId
                );
    }

    @Test
    @DisplayName("프로젝트 지원서 목록을 조회한다")
    void getProjectApplications() throws Exception {
        Long projectId = 100L;
        CompanyProjectApplicationResponse application =
                new CompanyProjectApplicationResponse(
                        300L,
                        10L,
                        "테스트 크루",
                        "crew-image.png",
                        null,
                        null,
                        null
                );

        given(companyWorkspaceService.getProjectApplications(COMPANY_ID, projectId))
                .willReturn(List.of(application));

        mockMvc.perform(get("/api/v1/companies/me/projects/{projectId}/applications", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 지원서 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].applicationId").value(300))
                .andExpect(jsonPath("$.payload[0].crewId").value(10))
                .andExpect(jsonPath("$.payload[0].crewName").value("테스트 크루"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjectApplications(COMPANY_ID, projectId);
    }

    @Test
    @DisplayName("프로젝트 지원서 상세를 조회한다")
    void getProjectApplicationDetail() throws Exception {
        Long projectId = 100L;
        Long applicationId = 300L;

        CompanyProjectApplicationDetailResponse response =
                new CompanyProjectApplicationDetailResponse(
                        applicationId,
                        10L,
                        "테스트 크루",
                        "crew-image.png",
                        null,
                        null,
                        "테스트 학교",
                        5,
                        "크루 소개",
                        "추가 소개",
                        List.of("장점1", "장점2"),
                        null,
                        "sns-link",
                        "etc-link",
                        "kakao-link",
                        "지원 소개",
                        "제안 내용",
                        null
                );

        given(companyWorkspaceService.getProjectApplicationDetail(COMPANY_ID, projectId, applicationId))
                .willReturn(response);

        mockMvc.perform(get(
                        "/api/v1/companies/me/projects/{projectId}/applications/{applicationId}",
                        projectId,
                        applicationId
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 지원서 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.applicationId").value(300))
                .andExpect(jsonPath("$.payload.crewId").value(10))
                .andExpect(jsonPath("$.payload.crewName").value("테스트 크루"))
                .andExpect(jsonPath("$.payload.memberAmount").value(5))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService)
                .getProjectApplicationDetail(COMPANY_ID, projectId, applicationId);
    }

    @Test
    @DisplayName("프로젝트 참여 크루를 선정한다")
    void selectProjectApplication() throws Exception {
        Long projectId = 100L;
        Long applicationId = 300L;

        CompanyProjectApplicationSelectResponse response =
                new CompanyProjectApplicationSelectResponse(
                        projectId,
                        applicationId,
                        10L,
                        null,
                        null
                );

        given(companyWorkspaceService.selectProjectApplication(COMPANY_ID, projectId, applicationId))
                .willReturn(response);

        mockMvc.perform(post(
                        "/api/v1/companies/me/projects/{projectId}/applications/{applicationId}/select",
                        projectId,
                        applicationId
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 참여 크루 선정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.applicationId").value(300))
                .andExpect(jsonPath("$.payload.selectedCrewId").value(10))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).selectProjectApplication(COMPANY_ID, projectId, applicationId);
    }

    @Test
    @DisplayName("파트너 크루를 조회한다")
    void getPartnerCrew() throws Exception {
        Long projectId = 100L;

        CompanyPartnerCrewResponse response =
                new CompanyPartnerCrewResponse(
                        projectId,
                        "테스트 프로젝트",
                        null,
                        10L,
                        "테스트 크루",
                        "crew-image.png",
                        null,
                        null,
                        "테스트 학교",
                        5,
                        "크루 소개",
                        "추가 소개",
                        List.of("장점1", "장점2"),
                        null,
                        "sns-link",
                        "etc-link",
                        "kakao-link"
                );

        given(companyWorkspaceService.getPartnerCrew(COMPANY_ID, projectId))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/projects/{projectId}/partner-crew", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("파트너 크루 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.crewId").value(10))
                .andExpect(jsonPath("$.payload.crewName").value("테스트 크루"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getPartnerCrew(COMPANY_ID, projectId);
    }

    @Test
    @DisplayName("기업이 특정 결과물에 수정 요청을 보낸다")
    void requestProjectSubmissionRevision() throws Exception {
        Long projectId = 100L;
        Long submissionId = 400L;

        CompanyProjectRevisionRequest request =
                new CompanyProjectRevisionRequest(
                        "영상 마지막 부분을 수정해주세요."
                );

        CompanyProjectRevisionResponse response =
                new CompanyProjectRevisionResponse(
                        projectId,
                        submissionId,
                        null,
                        null,
                        "영상 마지막 부분을 수정해주세요."
                );

        given(
                companyWorkspaceService
                        .requestProjectRevision(
                                eq(COMPANY_ID),
                                eq(projectId),
                                eq(submissionId),
                                eq(request)
                        )
        ).willReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/projects/{projectId}/submissions/{submissionId}/revision-requests",
                                projectId,
                                submissionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 결과물 수정 요청에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.payload.submissionId")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.payload.revisionReason")
                                .value(
                                        "영상 마지막 부분을 수정해주세요."
                                )
                )
                .andExpect(
                        jsonPath("$.hasNotification")
                                .value(false)
                );

        verify(companyWorkspaceService)
                .requestProjectRevision(
                        eq(COMPANY_ID),
                        eq(projectId),
                        eq(submissionId),
                        eq(request)
                );
    }

    @Test
    @DisplayName("기업이 특정 결과물을 승인한다")
    void approveProjectSubmission() throws Exception {
        Long projectId = 100L;
        Long submissionId = 400L;

        CompanyProjectApprovalResponse response =
                new CompanyProjectApprovalResponse(
                        projectId,
                        submissionId,
                        null,
                        null
                );

        given(
                companyWorkspaceService
                        .approveProject(
                                COMPANY_ID,
                                projectId,
                                submissionId
                        )
        ).willReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/projects/{projectId}/submissions/{submissionId}/approval",
                                projectId,
                                submissionId
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 결과물 승인에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.payload.submissionId")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.hasNotification")
                                .value(false)
                );

        verify(companyWorkspaceService)
                .approveProject(
                        COMPANY_ID,
                        projectId,
                        submissionId
                );
    }

    @Test
    @DisplayName("정산 프로젝트 목록을 조회한다")
    void getSettlements() throws Exception {
        CompanySettlementResponse settlement =
                new CompanySettlementResponse(
                        500L,
                        100L,
                        "테스트 프로젝트",
                        null,
                        10L,
                        "테스트 크루",
                        100000L,
                        null,
                        LocalDate.of(2026, 8, 1),
                        null
                );

        given(companyWorkspaceService.getSettlements(COMPANY_ID, null))
                .willReturn(List.of(settlement));

        mockMvc.perform(get("/api/v1/companies/me/settlements"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("정산 프로젝트 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].settlementId").value(500))
                .andExpect(jsonPath("$.payload[0].projectId").value(100))
                .andExpect(jsonPath("$.payload[0].projectName").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.payload[0].crewId").value(10))
                .andExpect(jsonPath("$.payload[0].crewName").value("테스트 크루"))
                .andExpect(jsonPath("$.payload[0].amount").value(100000))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getSettlements(COMPANY_ID, null);
    }

    @Test
    @DisplayName("정산 예상 지급일을 수정한다")
    void updateSettlementExpectedPaymentDate() throws Exception {
        Long settlementId = 500L;

        CompanySettlementExpectedPaymentDateRequest request =
                new CompanySettlementExpectedPaymentDateRequest(
                        LocalDate.of(2026, 8, 1)
                );

        CompanySettlementExpectedPaymentDateResponse response =
                new CompanySettlementExpectedPaymentDateResponse(
                        settlementId,
                        100L,
                        null,
                        LocalDate.of(2026, 8, 1)
                );

        given(companyWorkspaceService.updateSettlementExpectedPaymentDate(
                eq(COMPANY_ID),
                eq(settlementId),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(patch(
                        "/api/v1/companies/me/settlements/{settlementId}/expected-payment-date",
                        settlementId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("예상 지급 날짜 설정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.settlementId").value(500))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.expectedPaymentDate").value("2026-08-01"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService)
                .updateSettlementExpectedPaymentDate(eq(COMPANY_ID), eq(settlementId), eq(request));
    }

    @Test
    @DisplayName("정산 지급 완료를 처리한다")
    void completeSettlement() throws Exception {
        Long settlementId = 500L;
        Long projectId = 100L;
        LocalDate settlementDate =
                LocalDate.of(2026, 7, 14);

        CompanySettlementCompleteRequest request =
                new CompanySettlementCompleteRequest(
                        settlementDate
                );

        CompanySettlementCompleteResponse response =
                new CompanySettlementCompleteResponse(
                        settlementId,
                        projectId,
                        ProjectSettlementStatus.PAID,
                        ProjectStatus.DONE,
                        settlementDate
                );

        given(
                companyWorkspaceService.completeSettlement(
                        eq(COMPANY_ID),
                        eq(settlementId),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlementId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("정산 지급 완료 처리에 성공했습니다.")
                )
                .andExpect(
                        jsonPath("$.payload.settlementId")
                                .value(500)
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.payload.settlementStatus")
                                .value("PAID")
                )
                .andExpect(
                        jsonPath("$.payload.projectStatus")
                                .value("DONE")
                )
                .andExpect(
                        jsonPath("$.payload.settlementDate")
                                .value("2026-07-14")
                )
                .andExpect(
                        jsonPath("$.hasNotification")
                                .value(false)
                );

        verify(companyWorkspaceService)
                .completeSettlement(
                        eq(COMPANY_ID),
                        eq(settlementId),
                        eq(request)
                );
    }

    @Test
    @DisplayName("정산 지급 완료 요청에 실제 지급일이 없으면 실패한다")
    void cannotCompleteSettlementWithoutSettlementDate()
            throws Exception {

        Long settlementId = 500L;

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlementId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(
                companyWorkspaceService,
                never()
        ).completeSettlement(
                eq(COMPANY_ID),
                eq(settlementId),
                any(CompanySettlementCompleteRequest.class)
        );
    }

    private CompanyProjectRequest createProjectRequest() {
        return new CompanyProjectRequest(
                "project-image.png",
                "테스트 브랜드",
                "담당자",
                "manager@test.com",
                "010-1234-5678",
                "테스트 프로젝트",
                "프로젝트 목표",
                ProjectType.APPTEST,
                "요구사항",
                "프로젝트 설명",
                "결과물 형태",
                "필수 제출 항목",
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),
                CrewType.CLUB,
                "필요 역량",
                "우대 조건",
                100000L,
                true,
                "인센티브 조건",
                List.of("file-link-1"),
                "reference-link"
        );
    }

    private CompanyWorkspaceProjectResponse createProjectResponse(Long projectId) {
        return new CompanyWorkspaceProjectResponse(
                projectId,
                "project-image.png",
                "테스트 프로젝트",
                "테스트 브랜드",
                null,
                null,
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),
                100000L,
                true,
                20
        );
    }

    private CompanyWorkspaceProjectDetailResponse createProjectDetailResponse(Long projectId) {
        return new CompanyWorkspaceProjectDetailResponse(
                projectId,
                "project-image.png",
                "테스트 브랜드",
                "테스트 프로젝트",
                "프로젝트 목표",
                null,
                "요구사항",
                "프로젝트 설명",
                "결과물 형태",
                "필수 제출 항목",
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),
                null,
                "필요 역량",
                "우대 조건",
                100000L,
                true,
                "인센티브 조건",
                List.of("file-link-1"),
                "reference-link",
                null,
                20,
                "담당자",
                "manager@test.com",
                "010-1234-5678",
                10L
        );
    }

    private CompanyProjectDraftResponse createProjectDraftResponse(Long draftId) {
        return new CompanyProjectDraftResponse(
                draftId,
                "project-image.png",
                "테스트 브랜드",
                "담당자",
                "manager@test.com",
                "010-1234-5678",
                "테스트 프로젝트",
                "프로젝트 목표",
                null,
                "요구사항",
                "프로젝트 설명",
                "결과물 형태",
                "필수 제출 항목",
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),
                null,
                "필요 역량",
                "우대 조건",
                100000L,
                true,
                "인센티브 조건",
                List.of("file-link-1"),
                "reference-link",
                null
        );
    }
}