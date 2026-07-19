package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.ResultFormRequestDTO;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.dto.company.request.CompanySettlementCompleteRequest;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.company.response.CompanyProjectIdResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
        ApiResponseFactory apiResponseFactory =
                new ApiResponseFactory(notificationRepository);

        CompanyWorkspaceController controller =
                new CompanyWorkspaceController(
                        companyWorkspaceService,
                        apiResponseFactory
                );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        lenient()
                .when(userDetails.getId())
                .thenReturn(COMPANY_ID);

        lenient()
                .when(
                        notificationRepository
                                .existsByreceiverIdAndIsRead(
                                        COMPANY_ID,
                                        false
                                )
                )
                .thenReturn(false);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(
                                    MethodParameter parameter
                            ) {
                                return parameter
                                        .hasParameterAnnotation(
                                                AuthenticationPrincipal.class
                                        );
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
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(
                                objectMapper
                        )
                )
                .build();
    }

    @Test
    @DisplayName("기업 워크스페이스 대시보드를 조회한다")
    void getDashboard() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        mockMvc.perform(
                        get(
                                "/api/v1/companies/me/workspace/dashboard"
                        )
                                .param("status", "PROGRESS")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
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
                                        "기업 워크스페이스 대시보드 조회에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.hasNotification")
                                .value(false)
                );

        verify(companyWorkspaceService).getDashboard(
                eq(COMPANY_ID),
                eq(ProjectStatus.PROGRESS),
                eq(startDate),
                eq(endDate),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("필터와 페이지네이션으로 기업 프로젝트 목록을 조회한다")
    void getProjects() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        given(
                companyWorkspaceService.getProjects(
                        eq(COMPANY_ID),
                        eq("프로젝트"),
                        eq(Industry.IT),
                        eq(CrewType.CLUB),
                        eq(startDate),
                        eq(endDate),
                        any(Pageable.class)
                )
        ).willReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 12),
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/companies/me/projects")
                                .param("keyword", "프로젝트")
                                .param("category", "IT")
                                .param("crewType", "CLUB")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("page", "0")
                                .param("size", "12")
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
                                        "기업 프로젝트 목록 조회에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.content")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.payload.content")
                                .isEmpty()
                );

        verify(companyWorkspaceService).getProjects(
                eq(COMPANY_ID),
                eq("프로젝트"),
                eq(Industry.IT),
                eq(CrewType.CLUB),
                eq(startDate),
                eq(endDate),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("기본 조건으로 기업 프로젝트 목록을 조회한다")
    void getProjectsWithDefaultCondition() throws Exception {
        given(
                companyWorkspaceService.getProjects(
                        eq(COMPANY_ID),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                )
        ).willReturn(
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 12),
                        0
                )
        );

        mockMvc.perform(
                        get("/api/v1/companies/me/projects")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.content")
                                .isEmpty()
                );

        verify(companyWorkspaceService).getProjects(
                eq(COMPANY_ID),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("기업 프로젝트 상세를 조회한다")
    void getProjectDetail() throws Exception {
        Long projectId = 100L;

        mockMvc.perform(
                        get(
                                "/api/v1/companies/me/projects/{projectId}",
                                projectId
                        )
                                .param("page", "1")
                                .param("size", "5")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "기업 프로젝트 상세 조회에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService).getProjectDetail(
                COMPANY_ID,
                projectId,
                1,
                5
        );
    }

    @Test
    @DisplayName("새 프로젝트를 등록한다")
    void createProject() throws Exception {
        CompanyProjectRequestDTO request =
                createProjectRequest();

        CompanyProjectIdResponse response =
                new CompanyProjectIdResponse(
                        100L,
                        null
                );

        given(
                companyWorkspaceService.createProject(
                        eq(COMPANY_ID),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        post("/api/v1/companies/me/projects")
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
                        jsonPath("$.message")
                                .value(
                                        "새 프로젝트 등록에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(100)
                );

        verify(companyWorkspaceService).createProject(
                COMPANY_ID,
                request
        );
    }

    @Test
    @DisplayName("프로젝트를 수정한다")
    void updateProject() throws Exception {
        Long projectId = 100L;
        CompanyProjectRequestDTO request =
                createProjectRequest();

        CompanyProjectIdResponse response =
                new CompanyProjectIdResponse(
                        projectId,
                        null
                );

        given(
                companyWorkspaceService.updateProject(
                        eq(COMPANY_ID),
                        eq(projectId),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/projects/{projectId}",
                                projectId
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
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 수정에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(100)
                );

        verify(companyWorkspaceService).updateProject(
                COMPANY_ID,
                projectId,
                request
        );
    }

    @Test
    @DisplayName("프로젝트를 삭제한다")
    void deleteProject() throws Exception {
        Long projectId = 100L;

        mockMvc.perform(
                        delete(
                                "/api/v1/companies/me/projects/{projectId}",
                                projectId
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 삭제에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService).deleteProject(
                COMPANY_ID,
                projectId
        );
    }

    @Test
    @DisplayName("프로젝트를 임시 저장한다")
    void createProjectDraft() throws Exception {
        CompanyProjectRequestDTO request =
                createProjectRequest();

        CompanyProjectIdResponse response =
                new CompanyProjectIdResponse(
                        200L,
                        null
                );

        given(
                companyWorkspaceService.createProjectDraft(
                        eq(COMPANY_ID),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/project-drafts"
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
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 임시저장에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(200)
                );

        verify(companyWorkspaceService).createProjectDraft(
                COMPANY_ID,
                request
        );
    }

    @Test
    @DisplayName("임시 저장 프로젝트를 수정한다")
    void updateProjectDraft() throws Exception {
        Long draftId = 200L;
        CompanyProjectRequestDTO request =
                createProjectRequest();

        CompanyProjectIdResponse response =
                new CompanyProjectIdResponse(
                        draftId,
                        null
                );

        given(
                companyWorkspaceService.updateProjectDraft(
                        eq(COMPANY_ID),
                        eq(draftId),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/project-drafts/{draftId}",
                                draftId
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
                        jsonPath("$.message")
                                .value(
                                        "임시저장 프로젝트 수정에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.projectId")
                                .value(200)
                );

        verify(companyWorkspaceService).updateProjectDraft(
                COMPANY_ID,
                draftId,
                request
        );
    }

    @Test
    @DisplayName("임시 저장 프로젝트를 조회한다")
    void getProjectDraft() throws Exception {
        Long draftId = 200L;

        mockMvc.perform(
                        get(
                                "/api/v1/companies/me/project-drafts/{draftId}",
                                draftId
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "임시저장 프로젝트 조회에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService).getProjectDraft(
                COMPANY_ID,
                draftId
        );
    }

    @Test
    @DisplayName("프로젝트 참여 크루를 선정한다")
    void selectProjectApplication() throws Exception {
        Long projectId = 100L;
        Long applicationId = 300L;

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/projects/{projectId}/applications/{applicationId}/select",
                                projectId,
                                applicationId
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "프로젝트 참여 크루 선정에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService)
                .selectProjectApplication(
                        COMPANY_ID,
                        projectId,
                        applicationId
                );
    }

    @Test
    @DisplayName("기존 기업 정산 목록을 조회한다")
    void getSettlements() throws Exception {
        given(
                companyWorkspaceService.getSettlements(
                        COMPANY_ID,
                        ProjectSettlementStatus.WAITING
                )
        ).willReturn(List.of());

        mockMvc.perform(
                        get("/api/v1/companies/me/settlements")
                                .param("status", "WAITING")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "정산 프로젝트 목록 조회에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload")
                                .isEmpty()
                );

        verify(companyWorkspaceService).getSettlements(
                COMPANY_ID,
                ProjectSettlementStatus.WAITING
        );
    }

    @Test
    @DisplayName("정산 예상 지급일을 수정한다")
    void updateSettlementExpectedPaymentDate()
            throws Exception {

        Long settlementId = 500L;

        CompanySettlementExpectedPaymentDateRequest request =
                new CompanySettlementExpectedPaymentDateRequest(
                        LocalDate.of(2026, 8, 1)
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/expected-payment-date",
                                settlementId
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
                        jsonPath("$.message")
                                .value(
                                        "예상 지급 날짜 설정에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService)
                .updateSettlementExpectedPaymentDate(
                        COMPANY_ID,
                        settlementId,
                        request
                );
    }

    @Test
    @DisplayName("정산 지급 완료를 처리한다")
    void completeSettlement() throws Exception {
        Long settlementId = 500L;

        CompanySettlementCompleteRequest request =
                new CompanySettlementCompleteRequest(
                        LocalDate.of(2026, 7, 14)
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlementId
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
                        jsonPath("$.message")
                                .value(
                                        "정산 지급 완료 처리에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService)
                .completeSettlement(
                        COMPANY_ID,
                        settlementId,
                        request
                );
    }

    @Test
    @DisplayName("실제 지급일 없이 정산 완료를 요청하면 실패한다")
    void cannotCompleteSettlementWithoutPaymentDate()
            throws Exception {

        Long settlementId = 500L;

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlementId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
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

    @Test
    @DisplayName("기업 정산 관리 현황을 조회한다")
    void getCompanySubsidyStatus() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        mockMvc.perform(
                        get("/api/v1/companies/me/adjustment")
                                .param("status", "WAITING")
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "정산 현황 조회에 성공했습니다."
                                )
                );

        verify(companyWorkspaceService)
                .getCompanySubsidyStatus(
                        eq(COMPANY_ID),
                        eq(ProjectSettlementStatus.WAITING),
                        eq(startDate),
                        eq(endDate),
                        any(Pageable.class)
                );
    }

    private CompanyProjectRequestDTO createProjectRequest() {
        return new CompanyProjectRequestDTO(
                "테스트 브랜드",
                "담당자",
                "manager@test.com",

                List.of("project-image.png"),

                "테스트 프로젝트",
                "프로젝트 설명",
                Industry.IT,
                ProjectType.APPTEST,

                List.of(
                        new ResultFormRequestDTO(
                                "유튜브",
                                "숏폼",
                                2,
                                "프로젝트 보고서"
                        )
                ),

                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                LocalDate.of(2026, 8, 5),

                100000L,
                true,
                "인센티브 조건",

                CrewType.CLUB,
                5,
                "필요 역량",
                "우대 조건",

                List.of(),
                List.of()
        );
    }
}
