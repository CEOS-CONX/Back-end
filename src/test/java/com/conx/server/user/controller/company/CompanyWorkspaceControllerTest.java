package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.ProjectInspectionFeedback;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.ResultForm;
import com.conx.server.project.domain.ResultFormRequestDTO;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.*;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.service.workspace.CompanyWorkspaceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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

    @Transactional
    String loginSettingCrew() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("kimdoes2143@naver.com", "1q2w3e4r!!");
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

    @Transactional
    String loginSettingAdmin() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("jclee@gmail.com", "1q2w3e4r!!");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

    @Transactional
    String loginSettingCompany() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("navernaver@gmail.com", "1q2w3e4r!!");
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

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
                .setCustomArgumentResolvers(
                        new HandlerMethodArgumentResolver() {
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
                        },
                        new PageableHandlerMethodArgumentResolver()
                )
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private CompanyWorkspaceDashboardResponse createMockDashboardResponse() {

        CompanyProjectStatusResponseDTO projectStatus = new CompanyProjectStatusResponseDTO(
                3L,  // recruiting
                5L,  // progress
                2L,  // waiting_inspection
                1L,  // waiting_settlement
                10L  // done
        );

        CompanyExpenditureStatusResponseDTO expenditureStatus = new CompanyExpenditureStatusResponseDTO(
                2L, 3L, 1L, 4L, 0L, 2L, 3L, 1L, 0L, 2L, 1L, 5L,  // jan ~ dec
                5_000_000  // expenditure
        );

        TodoProjectWrapperDTO project1 = new TodoProjectWrapperDTO(
                1L,
                ProjectStatus.RECRUITING,
                "테스트 프로젝트 A",
                "테스트 브랜드",
                LocalDate.of(2026, 6, 1)
        );

        TodoProjectWrapperDTO project2 = new TodoProjectWrapperDTO(
                2L,
                ProjectStatus.PROGRESS,
                "테스트 프로젝트 B",
                "테스트 브랜드2",
                LocalDate.of(2026, 6, 10)
        );

        List<CompanyTodoProjectResponseDTO> todoProjectsStatus = List.of(
                new CompanyTodoProjectResponseDTO(ProjectStatus.RECRUITING, List.of(project1)),
                new CompanyTodoProjectResponseDTO(ProjectStatus.PROGRESS, List.of(project2)),
                new CompanyTodoProjectResponseDTO(ProjectStatus.CONTRACT_PENDING, List.of()),
                new CompanyTodoProjectResponseDTO(ProjectStatus.INSPECTION, List.of()),
                new CompanyTodoProjectResponseDTO(ProjectStatus.ADJUSTING, List.of()),
                new CompanyTodoProjectResponseDTO(ProjectStatus.DONE, List.of())
        );

        return CompanyWorkspaceDashboardResponse.of(
                projectStatus, expenditureStatus, todoProjectsStatus
        );
    }

    @Test
    @DisplayName("기업 대시보드 조회")
    void getCompanyDashboard() throws Exception {
        CompanyWorkspaceDashboardResponse mockResponse = createMockDashboardResponse();

        given(companyWorkspaceService.getDashboard(eq(COMPANY_ID), any(), any(), any(), any()))
                .willReturn(mockResponse);

        mockMvc.perform(get("/api/v1/companies/me/workspace/dashboard")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.projectStatus.recruiting").value(3))
                .andExpect(jsonPath("$.payload.expenditureStatus.expenditure").value(5000000))
                .andExpect(jsonPath("$.payload.todoProjectsStatus[0].status").value("RECRUITING"))
                .andExpect(jsonPath("$.payload.todoProjectsStatus[0].projects[0].projectName").value("테스트 프로젝트 A"));
    }

    @Test
    @DisplayName("기업 프로젝트 목록 필터링 조회")
    void getCompanyProjectsWithFiltering() throws Exception {
        // given
        CompanyWorkspaceProjectResponse project1 = new CompanyWorkspaceProjectResponse(
                1L,
                5L,
                ProjectStatus.RECRUITING,
                List.of("https://example.com/image1.jpg"),
                "프로젝트 A",
                "브랜드 A",
                Industry.FASHION,
                ProjectType.CAMPAIGN,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1),
                100
        );

        CompanyWorkspaceProjectResponse project2 = new CompanyWorkspaceProjectResponse(
                2L,
                10L,
                ProjectStatus.PROGRESS,
                List.of("https://example.com/image2.jpg"),
                "프로젝트 B",
                "브랜드 B",
                Industry.FASHION,
                ProjectType.CAMPAIGN,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 7, 10),
                50
        );

        Page<CompanyWorkspaceProjectResponse> mockPage =
                new PageImpl<>(List.of(project1, project2), PageRequest.of(0, 12), 2);

        given(companyWorkspaceService.getProjects(
                eq(COMPANY_ID), any(), any(), any(), any(), any(), any()
        )).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/projects")
                        .param("keyword", "프로젝트")
                        .param("category", "FASHION")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-07-31")
                        .param("page", "0")
                        .param("size", "12"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(2))
                .andExpect(jsonPath("$.payload.content[0].name").value("프로젝트 A"))
                .andExpect(jsonPath("$.payload.content[0].deadlineCount").value(5))
                .andExpect(jsonPath("$.payload.content[1].name").value("프로젝트 B"))
                .andExpect(jsonPath("$.payload.totalElements").value(2));
    }

    @Test
    @DisplayName("기본조건으로 기업 프로젝트 목록 조회")
    void getCompanyProjectWithDefault() throws Exception {
        // given
        Page<CompanyWorkspaceProjectResponse> emptyPage =
                new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);

        given(companyWorkspaceService.getProjects(
                eq(COMPANY_ID), isNull(), isNull(), isNull(), isNull(), isNull(), any()
        )).willReturn(emptyPage);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/projects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(0));
    }

    @Test
    @DisplayName("기본조건으로 기업 정산 현황 조회")
    void getCompanySubsidyStatusWithDefault() throws Exception {
        // given
        SubsidyStatusResponse response = new SubsidyStatusResponse(
                new SubsidyStatusWrapperDTO(0L, 0L, null, 0L),
                List.of()
        );

        given(companyWorkspaceService.getCompanySubsidyStatus(
                eq(COMPANY_ID), isNull(), isNull(), isNull(), any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/adjustment"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.subsidyStatus.totalSubsidy").value(0))
                .andExpect(jsonPath("$.payload.subsidyStatus.expectedSubsidy").value(0))
                .andExpect(jsonPath("$.payload.subsidyStatus.nextExpectedSubsidy").isEmpty())
                .andExpect(jsonPath("$.payload.subsidyStatus.thisMonthSubsidy").value(0))
                .andExpect(jsonPath("$.payload.adjustmentList.length()").value(0));
    }

    @Test
    @DisplayName("status, 기간 조건으로 기업 정산 현황 조회")
    void getCompanySubsidyStatusWithFilters() throws Exception {
        // given
        ProjectSettlementStatus status = ProjectSettlementStatus.WAITING;
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        SubsidyStatusResponse response = new SubsidyStatusResponse(
                new SubsidyStatusWrapperDTO(1_000_000L, 500_000L, LocalDate.of(2026, 8, 1), 200_000L),
                List.of()
        );

        given(companyWorkspaceService.getCompanySubsidyStatus(
                eq(COMPANY_ID), eq(status), eq(startDate), eq(endDate), any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/adjustment")
                        .param("status", status.name())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.subsidyStatus.totalSubsidy").value(1_000_000))
                .andExpect(jsonPath("$.payload.subsidyStatus.expectedSubsidy").value(500_000))
                .andExpect(jsonPath("$.payload.subsidyStatus.nextExpectedSubsidy").value("2026-08-01"))
                .andExpect(jsonPath("$.payload.subsidyStatus.thisMonthSubsidy").value(200_000));
    }

    @Test
    @DisplayName("페이지네이션 파라미터를 적용해서 기업 정산 현황 조회")
    void getCompanySubsidyStatusWithPaging() throws Exception {
        // given
        SubsidyStatusResponse response = new SubsidyStatusResponse(
                new SubsidyStatusWrapperDTO(0L, 0L, null, 0L),
                List.of()
        );

        given(companyWorkspaceService.getCompanySubsidyStatus(
                eq(COMPANY_ID), isNull(), isNull(), isNull(), any()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/adjustment")
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk());
    }

/*
    @Test
    @DisplayName("기업 프로젝트 상세를 조회한다")
    void getProjectDetail() throws Exception {
        Long projectId = 100L;
        CompanyWorkspaceProjectDetailResponse response = createProjectDetailResponse(projectId);

        given(companyWorkspaceService.getProjectDetail(eq(COMPANY_ID), eq(projectId), anyInt(), anyInt()))
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

        verify(companyWorkspaceService).getProjectDetail(eq(COMPANY_ID), eq(projectId), anyInt(), anyInt());
    }

 */

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

    /*
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

     */

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


    private CompanyWorkspaceProjectDetailResponse createProjectDetailResponse(Long projectId) {
        Crew crew = mock(Crew.class);
        given(crew.getProfileImage()).willReturn("crew-image.png");

        CompanyProjectDetailResponse common = new CompanyProjectDetailResponse(
                projectId,
                ProjectStatus.PROGRESS,
                "테스트 프로젝트",
                "테스트 브랜드",
                "담당자",
                "manager@test.com",
                crew,
                "crew-image.png",
                "크루 이름",
                null,
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),
                null,
                List.of()
        );

        // 여기! 이 줄을 바꿔주세요
        Page<InspectionInfoInOneLineDTO> inspections =
                new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);

        return ProjectStatusResponseDTO.create(common, inspections);
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