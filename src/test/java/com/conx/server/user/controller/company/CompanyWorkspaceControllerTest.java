package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.ResultForm;
import com.conx.server.project.domain.ResultFormRequestDTO;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.CompanyPartnerCrewResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationDetailResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApplicationSelectResponse;
import com.conx.server.user.dto.company.response.CompanyProjectApprovalResponse;
import com.conx.server.user.dto.company.response.CompanyProjectDraftResponse;
import com.conx.server.user.dto.company.response.CompanyProjectIdResponse;
import com.conx.server.user.dto.company.response.CompanyProjectRevisionResponse;
import com.conx.server.user.dto.company.response.CompanySettlementExpectedPaymentDateResponse;
import com.conx.server.user.dto.company.response.CompanySettlementResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceDashboardResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectResponse;
import com.conx.server.user.service.workspace.CompanyWorkspaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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

        given(userDetails.getId()).willReturn(COMPANY_ID);
        given(notificationRepository.existsByreceiverIdAndIsRead(COMPANY_ID, false))
                .willReturn(false);

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
                .andExpect(jsonPath("$.payload.projectName").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.payload.brandName").value("테스트 브랜드"))
                .andExpect(jsonPath("$.payload.selectedCrewId").value(10))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjectDetail(COMPANY_ID, projectId);
    }

    @Test
    @DisplayName("새 프로젝트를 등록한다")
    void createProject() throws Exception {
        CompanyProjectRequestDTO request = createProjectRequest();
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
        CompanyProjectRequestDTO request = createProjectRequest();
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
        CompanyProjectRequestDTO request = createProjectRequest();
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
        CompanyProjectRequestDTO request = createProjectRequest();
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
                .andExpect(jsonPath("$.payload.projectName").value("테스트 프로젝트"))
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
                .andExpect(jsonPath("$.payload.projectName").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).getProjectReviewDetail(COMPANY_ID, projectId);
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
    @DisplayName("프로젝트 결과물 수정 요청을 보낸다")
    void requestProjectRevision() throws Exception {
        Long projectId = 100L;

        CompanyProjectRevisionRequest request =
                new CompanyProjectRevisionRequest("수정이 필요합니다.");

        CompanyProjectRevisionResponse response =
                new CompanyProjectRevisionResponse(
                        projectId,
                        400L,
                        null,
                        null,
                        "수정이 필요합니다."
                );

        given(companyWorkspaceService.requestProjectRevision(
                eq(COMPANY_ID),
                eq(projectId),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(post("/api/v1/companies/me/projects/{projectId}/revision-requests", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 결과물 수정 요청에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.submissionId").value(400))
                .andExpect(jsonPath("$.payload.revisionReason").value("수정이 필요합니다."))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService)
                .requestProjectRevision(eq(COMPANY_ID), eq(projectId), eq(request));
    }

    @Test
    @DisplayName("프로젝트 결과물을 승인한다")
    void approveProject() throws Exception {
        Long projectId = 100L;

        CompanyProjectApprovalResponse response =
                new CompanyProjectApprovalResponse(
                        projectId,
                        400L,
                        null,
                        null
                );

        given(companyWorkspaceService.approveProject(COMPANY_ID, projectId))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/companies/me/projects/{projectId}/approval", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 결과물 승인에 성공했습니다."))
                .andExpect(jsonPath("$.payload.projectId").value(100))
                .andExpect(jsonPath("$.payload.submissionId").value(400))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyWorkspaceService).approveProject(COMPANY_ID, projectId);
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
                        LocalDate.of(2026, 8, 1)
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

    private CompanyProjectRequestDTO createProjectRequest() {
        return new CompanyProjectRequestDTO(
                "테스트 브랜드",                // brandName
                "담당자",                     // managerName
                "manager@test.com",          // managerEmail

                List.of("project-image.png"), // projectImages

                "테스트 프로젝트",             // projectName
                "프로젝트 설명",               // projectExplanation
                Industry.IT,                  // industry
                ProjectType.APPTEST,          // projectType
                List.of(new ResultFormRequestDTO(
                        "유튜브",              // platform
                        "숏폼",               // contentType
                        2,                    // numberOfResult
                        "프로젝트 보고서"       // finalResult
                )),

                LocalDate.of(2026, 6, 30),    // recruitDeadline
                LocalDate.of(2026, 7, 1),     // projectStartDate
                LocalDate.of(2026, 7, 31),    // projectDeadline
                LocalDate.of(2026, 8, 5),     // submitDeadline

                100000L,                      // subsidy
                true,                         // incentive
                "인센티브 조건",                // incentiveCondition

                CrewType.CLUB,                // crewType
                5,                            // peopleNumber
                "필요 역량",                   // competency
                "우대 조건",                   // preferenceCondition

                null,                         // fileLink(or fileKey)
                null
        );
    }

    private CompanyWorkspaceProjectResponse createProjectResponse(Long projectId) {
        return new CompanyWorkspaceProjectResponse(
                projectId,
                List.of("project-image.png"),
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
                List.of("project-image.png"),

                "테스트 브랜드",
                "테스트 프로젝트",
                "프로젝트 설명",

                null, // ProjectType
                List.of(), // resultForm

                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),

                null, // CrewType
                5,    // peopleNumber
                "필요 역량",
                "우대 조건",

                100000L,
                true,
                "인센티브 조건",

                List.of(), // List<FileResponseDTO>
                List.of(), // List<AdditionalLinksWrapper>

                null, // ProjectStatus
                20,

                "담당자",
                "manager@test.com",

                10L // selectedCrewId
        );
    }

    private CompanyProjectDraftResponse createProjectDraftResponse(Long draftId) {
        return new CompanyProjectDraftResponse(
                draftId,
                List.of("project-image.png"),

                "테스트 브랜드",
                "담당자",
                "manager@test.com",

                "테스트 프로젝트",
                "프로젝트 설명",

                null, // ProjectType
                List.of(), // resultForm

                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),

                null, // CrewType
                5,    // peopleNumber

                "필요 역량",
                "우대 조건",

                100000L,
                true,
                "인센티브 조건",

                List.of(), // List<FileResponseDTO>
                List.of(), // List<AdditionalLinksWrapper>

                null // ProjectStatus
        );
    }
}