package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.CrewInfoForProjectApplicationDTO;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.company.request.CompanySettlementCompleteRequest;
import com.conx.server.user.dto.company.response.CompanySettlementResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.ProjectApplicationForCompanyWrapperDTO;
import com.conx.server.user.dto.company.response.ProjectStatusResponseDTO;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.*;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginResponseDTO;
import com.conx.server.user.repository.AdminRepository;
import com.conx.server.user.repository.CrewRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.conx.server.user.dto.crew.CrewTodoProgressStatus;
import com.conx.server.user.dto.company.request.CompanySettlementExpectedPaymentDateRequest;
import com.conx.server.user.dto.crew.response.CrewSettlementSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.util.List;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class CrewWorkSpaceTest {
    @Autowired
    private CrewRepository crewRepository;

    @Transactional
    String loginSetting() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("kimdoes2143@naver.com", "1q2w3e4r!!");
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

    @Transactional
    String loginSetting_Admin() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("jclee@gmail.com", "1q2w3e4r!!");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

    @Transactional
    String loginSetting_Company() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("navernaver@gmail.com", "1q2w3e4r!!");
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

    @Transactional
    protected long applyProjectAndGetApplicationId(
            String crewToken,
            long projectId
    ) throws Exception {

        ProjectApplicationRequest request =
                new ProjectApplicationRequest(
                        "프로젝트 지원 소개"
                );

        MvcResult mvcResult =
                mockMvc.perform(
                                post(
                                        "/api/v1/projects/{projectId}/applications",
                                        projectId
                                )
                                        .header(
                                                "Authorization",
                                                crewToken
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        request
                                                )
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<ProjectApplicationResponse> response =
                objectMapper.readValue(
                        mvcResult.getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<ProjectApplicationResponse>
                                >() {
                        }
                );

        return response.payload()
                .applicationId();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AdminRepository adminRepository;
    //회원가입 테스트 진행하기

    //com.conx.server.global.common.SettingUserForTest.java 파일에서 테스트의 경우
    //프로젝트와 크루가 이미 회원가입 되어 있습니다.
    @Test
    @Transactional
    @DisplayName("크루 로그인")
    void login() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("kimdoes2143@naver.com", "1q2w3e4r!!");
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<LoginResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<LoginResponseDTO>>() {
                }
        );

        LoginResponseDTO loginResponse = response.payload();
        assertThat(loginResponse.email()).isEqualTo(req.email());
        assertThat(loginResponse.userType()).isEqualTo(UserRole.CREW);
        assertThat(loginResponse.hasFullInfo()).isEqualTo(Boolean.FALSE);
    }

    @Test
    @Transactional
    @DisplayName("크루 랜딩페이지 진입")
    void crewLandingPage() throws Exception {
        String token = loginSetting();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/landing/crew")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<ProjectWrapperForLandingPageDTO>> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<List<ProjectWrapperForLandingPageDTO>>>() {
                }
        );

        List<ProjectWrapperForLandingPageDTO> landingResponse = response.payload();

        assertThat(landingResponse.size()).isEqualTo(7);
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트들 보기")
    void browseProjects() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(6));
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 검색어 조회")
    void browseProjectsWithQuery() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?" +
                        "keyword=코카콜라" +
                        "&page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(1));
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 카테고리 조회")
    void browseProjectsWithCategory() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?" +
                        "category=IT" +
                        "&page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(2));
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 타입 기준 조회")
    void browseProjectsWithProjectType() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?" +
                        "projectType=APPTEST" +
                        "&page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(5));
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 시작일 기준 조회")
    void browseProjectsWithStartDate() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?" +
                        "startDate=2025-06-20" +
                        "&page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(6));
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 시작&마감일 기준 조회")
    void browseProjectsWithStartDateAndEndDate() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?" +
                        "startDate=2025-06-20" +
                        "&endDate=2025-08-10" +
                        "&page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(0));
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 상세보기")
    void getDetailedProjects() throws Exception {
        String token = loginSetting();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/projects/1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectBrowseDetailResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectBrowseDetailResponse>>() {
                }
        );

        ProjectBrowseDetailResponse detailResponse = response.payload();
        assertThat(detailResponse.projectId()).isEqualTo(1);
        assertThat(detailResponse.brandName()).isEqualTo("네이버");
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원 전 본인의 정보 확인하기")
    void getMyInfoBeforeApplication() throws Exception {
        String token = loginSetting();
        Crew crew = crewRepository.findByEmail("kimdoes2143@naver.com").get();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/projects/applications/my-info")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewInfoForProjectApplicationDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewInfoForProjectApplicationDTO>>() {
                }
        );

        CrewInfoForProjectApplicationDTO info = response.payload();
        assertThat(info.crewName()).isEqualTo(crew.getCrewName());
        assertThat(info.managerName()).isEqualTo(crew.getManagerName());
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원하기")
    void applyProject() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        ProjectApplicationResponse detailResponse = response.payload();
        assertThat(detailResponse.projectId()).isEqualTo(1);
        assertThat(detailResponse.status()).isEqualTo(ProjectApplicationStatus.PENDING);
        assertThat(response.hasNotification()).isEqualTo(false);
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원하기 + 알림확인")
    void selectCrewAndGetNotification() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        long applicationId = response.payload().applicationId();

        //프로젝트 크루 선정
        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        //재확인
        MvcResult mvcResult2 = mockMvc.perform(get("/api/v1/crews/applications?status=ALL")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewApplicationStatusResponseDTO> response2 = objectMapper.readValue(
                mvcResult2.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {
                }
        );

        Project project = projectRepository.findById(1L).get();

        CrewApplicationStatusResponseDTO applicationResponse = response2.payload();
        assertThat(applicationResponse.applications().get(0).applicationId()).isEqualTo(applicationId);
        assertThat(applicationResponse.applications().get(0).status()).isEqualTo(ProjectApplicationStatus.SELECTED);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.CONTRACT_PENDING);
        assertThat(response2.hasNotification()).isEqualTo(true);
    }

    @Transactional
    void completeProjectApplicationToContract() throws Exception {
        String token = loginSetting();
        String tokenCompany = loginSetting_Company();
        String token_admin = loginSetting_Admin();

        //프로젝트 지원하기
        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        long applicationId = response.payload().applicationId();

        //프로젝트 크루 선정
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        //프로젝트 계약서 작성 완료 선정
        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", token_admin))
                .andExpect(status().isOk());
    }

    @Transactional
    protected CompanySettlementResponse createWaitingSettlement(
            String crewToken,
            String companyToken
    ) throws Exception {

        completeProjectApplicationToContract();

        SubmitProjectResultRequestDTO submissionRequest =
                new SubmitProjectResultRequestDTO(
                        List.of("result-file"),
                        "프로젝트 결과물입니다."
                );

        /*
         * 결과물 제출
         * PROGRESS → INSPECTION
         */
        mockMvc.perform(
                        post("/api/v1/crews/projects/1/submissions")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                submissionRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 기업 승인
         * INSPECTION → ADJUSTING
         * WAITING 정산 생성
         */
        long submissionId =
                getLatestSubmissionId(
                        companyToken,
                        1L
                );

        registerFeedback(
                companyToken,
                1L,
                submissionId
        );

        MvcResult mvcResult =
                mockMvc.perform(
                                get(
                                        "/api/v1/companies/me/settlements"
                                )
                                        .header(
                                                "Authorization",
                                                companyToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<List<CompanySettlementResponse>> response =
                objectMapper.readValue(
                        mvcResult.getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<
                                        List<CompanySettlementResponse>
                                        >
                                >() {
                        }
                );

        return response.payload()
                .stream()
                .filter(settlement ->
                        settlement.projectId() == 1L
                )
                .findFirst()
                .orElseThrow();
    }

    @Transactional
    protected void registerFeedback(
            String companyToken,
            long projectId,
            long submissionId
    ) throws Exception {
        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/projects/{projectId}/submissions/{submissionId}/feedback",
                                projectId,
                                submissionId
                        )
                                .header("Authorization", companyToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "content": "결과물 검수를 완료했습니다.",
                                          "files": [],
                                          "links": []
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }

    @Transactional
    protected CrewDashboardResultDTO getCrewDashboard(
            String token
    ) throws Exception {

        MvcResult mvcResult =
                mockMvc.perform(
                                get("/api/v1/crews/dashboard")
                                        .header(
                                                "Authorization",
                                                token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<CrewDashboardResultDTO> response =
                objectMapper.readValue(
                        mvcResult.getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<CrewDashboardResultDTO>
                                >() {
                        }
                );

        return response.payload();
    }

    @Transactional
    protected long getLatestSubmissionId(
            String companyToken,
            long projectId
    ) throws Exception {

        MvcResult mvcForCompanyProject1 = mockMvc.perform(get("/api/v1/companies/me/projects/" + projectId)
                        .header("Authorization", companyToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CompanyWorkspaceProjectDetailResponse> resForCompanyProject1 = objectMapper.readValue(
                mvcForCompanyProject1.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CompanyWorkspaceProjectDetailResponse>>() {
                }
        );

        ProjectStatusResponseDTO resForCompanyProjectDTO = (ProjectStatusResponseDTO) resForCompanyProject1.payload();
        return resForCompanyProjectDTO.inspections().get(resForCompanyProjectDTO.inspections().size() - 1).inspectionId();

    }

    @Transactional
    protected CrewSettlementSummaryResponse getCrewSettlementSummary(
            String crewToken
    ) throws Exception {

        MvcResult mvcResult =
                mockMvc.perform(
                                get("/api/v1/crews/settlements/summary")
                                        .header(
                                                "Authorization",
                                                crewToken
                                        )
                        )
                        .andReturn();

        ApiResponse<CrewSettlementSummaryResponse> response =
                objectMapper.readValue(
                        mvcResult.getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<
                                        CrewSettlementSummaryResponse
                                        >
                                >() {
                        }
                );

        return response.payload();
    }

    @Test
    @Transactional
    @DisplayName("초기상태 대시보드")
    void initialDashboard() throws Exception {
        String token = loginSetting();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/dashboard")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewDashboardResultDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewDashboardResultDTO>>() {
                }
        );

        CrewDashboardResultDTO resultDTO = response.payload();
        CrewProjectInfoDTO projectInfoDTO = resultDTO.projectInfo();

        assertThat(resultDTO.totalSubsidy()).isEqualTo(0);

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.executionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.submissionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.settlementCompletedProjectAmount()).isEqualTo(0);

        assertThat(resultDTO.evaluation().overall()).isEqualTo(0.0);
        assertThat(resultDTO.evaluation().completeness()).isEqualTo(0.0);
        assertThat(resultDTO.evaluation().ability()).isEqualTo(0.0);
        assertThat(resultDTO.evaluation().communication()).isEqualTo(0.0);
        assertThat(resultDTO.evaluation().schedule()).isEqualTo(0.0);
        assertThat(resultDTO.evaluation().reCooperation()).isEqualTo(0.0);

        assertThat(resultDTO.todoProjects()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("초기상태 프로젝트 지원현황")
    void initialApplicationStatus() throws Exception {
        String token = loginSetting();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/applications?status=ALL")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewApplicationStatusResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {
                }
        );

        CrewApplicationStatusResponseDTO resultDTO = response.payload();

        assertThat(resultDTO.applications()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원 후 대시보드")
    void dashboardAfterApplication() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());


        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/dashboard")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewDashboardResultDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewDashboardResultDTO>>() {
                }
        );

        CrewDashboardResultDTO resultDTO = response.payload();
        CrewProjectInfoDTO projectInfoDTO = resultDTO.projectInfo();

        assertThat(resultDTO.totalSubsidy()).isEqualTo(0);

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.executionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.submissionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.settlementCompletedProjectAmount()).isEqualTo(0);

        assertThat(resultDTO.todoProjects()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원 후 프로젝트 지원현황")
    void applicationStatusAfterApplication() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());


        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/applications?status=PENDING")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewApplicationStatusResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {
                }
        );

        CrewApplicationStatusResponseDTO resultDTO = response.payload();

        assertThat(resultDTO.applications().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 선정 후 대시보드")
    void dashboardAfterSelectedProject() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        MvcResult applicationMvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> applicationResponse = objectMapper.readValue(
                applicationMvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        long applicationId = applicationResponse.payload().applicationId();


        //선정하기
        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/dashboard")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewDashboardResultDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewDashboardResultDTO>>() {
                }
        );

        CrewDashboardResultDTO resultDTO = response.payload();
        CrewProjectInfoDTO projectInfoDTO = resultDTO.projectInfo();

        assertThat(resultDTO.totalSubsidy()).isEqualTo(0);

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.executionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.submissionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.settlementCompletedProjectAmount()).isEqualTo(0);

        assertThat(resultDTO.todoProjects()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 선정 후 프로젝트 지원현황")
    void applicationStatusAfterSelected() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        MvcResult applicationMvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> applicationResponse = objectMapper.readValue(
                applicationMvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        long applicationId = applicationResponse.payload().applicationId();

        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        //선정하기
        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());


        //조회
        MvcResult mvcResult1 = mockMvc.perform(get("/api/v1/crews/applications?status=PENDING")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(get("/api/v1/crews/applications?status=SELECTED")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewApplicationStatusResponseDTO> response1 = objectMapper.readValue(
                mvcResult1.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {
                }
        );

        ApiResponse<CrewApplicationStatusResponseDTO> response2 = objectMapper.readValue(
                mvcResult2.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {
                }
        );

        CrewApplicationStatusResponseDTO resultDTO1 = response1.payload();
        CrewApplicationStatusResponseDTO resultDTO2 = response2.payload();

        assertThat(resultDTO1.applications().size()).isEqualTo(1);
        assertThat(resultDTO2.applications().size()).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 계약서 작성 후 대시보드")
    void dashboardAfterContract() throws Exception {
        String token = loginSetting();
        completeProjectApplicationToContract();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/dashboard")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewDashboardResultDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewDashboardResultDTO>>() {
                }
        );

        CrewDashboardResultDTO resultDTO = response.payload();
        CrewProjectInfoDTO projectInfoDTO = resultDTO.projectInfo();

        assertThat(resultDTO.totalSubsidy()).isEqualTo(0);

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.executionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.submissionCompletedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.settlementCompletedProjectAmount()).isEqualTo(0);

        assertThat(resultDTO.todoProjects()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 상태 전환에 따라 대시보드 집계와 누적 지원금이 변경된다")
    void dashboardChangesByProjectStatusAndSettlement()
            throws Exception {

        String crewToken = loginSetting();
        String companyToken = loginSetting_Company();

        completeProjectApplicationToContract();

        /*
         * 1. 계약 완료 후 PROGRESS
         * 진행 중 1
         */
        CrewDashboardResultDTO progressDashboard =
                getCrewDashboard(crewToken);

        assertThat(
                progressDashboard.projectInfo()
                        .appliedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                progressDashboard.projectInfo()
                        .progressProjectAmount()
        ).isEqualTo(1);

        assertThat(
                progressDashboard.projectInfo()
                        .executionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                progressDashboard.projectInfo()
                        .submissionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                progressDashboard.projectInfo()
                        .settlementCompletedProjectAmount()
        ).isEqualTo(0);

        /*
         * 2. 결과물 제출 후 INSPECTION
         * 제출 완료 1
         */
        SubmitProjectResultRequestDTO submissionRequest =
                new SubmitProjectResultRequestDTO(
                        List.of(
                                "result-file-1",
                                "result-file-2"
                        ),
                        "프로젝트 결과물입니다."
                );

        mockMvc.perform(
                        post(
                                "/api/v1/crews/projects/1/submissions"
                        )
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                submissionRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        CrewDashboardResultDTO inspectionDashboard =
                getCrewDashboard(crewToken);

        assertThat(
                inspectionDashboard.projectInfo()
                        .progressProjectAmount()
        ).isEqualTo(0);

        assertThat(
                inspectionDashboard.projectInfo()
                        .executionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                inspectionDashboard.projectInfo()
                        .submissionCompletedProjectAmount()
        ).isEqualTo(1);

        assertThat(
                inspectionDashboard.projectInfo()
                        .settlementCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                inspectionDashboard.todoProjects()
        ).isEmpty();

        /*
         * 3. 승인 API가 제거된 최신 흐름에서는 피드백 전까지 INSPECTION 유지
         */

        long submissionId =
                getLatestSubmissionId(
                        companyToken,
                        1L
                );



        CrewDashboardResultDTO waitingResultDashboard =
                getCrewDashboard(crewToken);

        assertThat(
                waitingResultDashboard.projectInfo()
                        .progressProjectAmount()
        ).isEqualTo(0);

        assertThat(
                waitingResultDashboard.projectInfo()
                        .executionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                waitingResultDashboard.projectInfo()
                        .submissionCompletedProjectAmount()
        ).isEqualTo(1);

        assertThat(
                waitingResultDashboard.projectInfo()
                        .settlementCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                waitingResultDashboard.todoProjects()
        ).isEmpty();

        /*
         * 4. 기업 피드백 등록으로 검수를 완료한다.
         * INSPECTION → ADJUSTING
         */
        registerFeedback(companyToken, 1L, submissionId);

        CrewDashboardResultDTO adjustingDashboard =
                getCrewDashboard(crewToken);

        assertThat(
                adjustingDashboard.projectInfo()
                        .executionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                adjustingDashboard.projectInfo()
                        .submissionCompletedProjectAmount()
        ).isEqualTo(1);

        assertThat(
                adjustingDashboard.projectInfo()
                        .settlementCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                adjustingDashboard.totalSubsidy()
        ).isEqualTo(0);

        assertThat(
                adjustingDashboard.todoProjects()
        ).hasSize(1);

        assertThat(
                adjustingDashboard.todoProjects()
                        .get(0)
                        .taskName()
        ).isEqualTo("정산 정보 확인");

        assertThat(
                adjustingDashboard.todoProjects()
                        .get(0)
                        .progressStatus()
        ).isEqualTo(
                CrewTodoProgressStatus.NEEDS_CONFIRMATION
        );

        /*
         * 6. 생성된 정산 정보 조회
         */
        MvcResult settlementMvcResult =
                mockMvc.perform(
                                get(
                                        "/api/v1/companies/me/settlements"
                                )
                                        .header(
                                                "Authorization",
                                                companyToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<List<CompanySettlementResponse>>
                settlementResponse =
                objectMapper.readValue(
                        settlementMvcResult.getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<
                                        List<CompanySettlementResponse>
                                        >
                                >() {
                        }
                );

        CompanySettlementResponse settlement =
                settlementResponse.payload()
                        .stream()
                        .filter(response ->
                                response.projectId() == 1L
                        )
                        .findFirst()
                        .orElseThrow();

        /*
         * 7. 정산 지급 완료
         * ProjectSettlement PAID
         * Project DONE
         */
        CompanySettlementCompleteRequest completeRequest =
                new CompanySettlementCompleteRequest(
                        LocalDate.of(2026, 7, 14)
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlement.settlementId()
                        )
                                .header(
                                        "Authorization",
                                        companyToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                completeRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 8. 정산 완료 후 최종 대시보드
         */
        CrewDashboardResultDTO paidDashboard =
                getCrewDashboard(crewToken);

        assertThat(
                paidDashboard.projectInfo()
                        .appliedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                paidDashboard.projectInfo()
                        .progressProjectAmount()
        ).isEqualTo(0);

        assertThat(
                paidDashboard.projectInfo()
                        .executionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                paidDashboard.projectInfo()
                        .submissionCompletedProjectAmount()
        ).isEqualTo(0);

        assertThat(
                paidDashboard.projectInfo()
                        .settlementCompletedProjectAmount()
        ).isEqualTo(1);

        assertThat(
                paidDashboard.totalSubsidy()
        ).isEqualTo(settlement.amount());

        assertThat(
                paidDashboard.todoProjects()
        ).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("초기 크루 워크스페이스")
    void initialsCrewWorkSpace() throws Exception {
        String token = loginSetting();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/workSpace")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewWorkSpaceResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewWorkSpaceResponseDTO>>() {
                }
        );

        CrewWorkSpaceResponseDTO responseDTO = response.payload();

        assertThat(responseDTO.projects()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원 후 워크스페이스")
    void workSpaceAfterApplication() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/workSpace")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewWorkSpaceResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewWorkSpaceResponseDTO>>() {
                }
        );

        CrewWorkSpaceResponseDTO responseDTO = response.payload();

        assertThat(responseDTO.projects()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 선정 후 크루 워크스페이스")
    void workSpaceAfterSelected() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        MvcResult applicationMvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> applicationResponse = objectMapper.readValue(
                applicationMvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        long applicationId = applicationResponse.payload().applicationId();


        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        //선정하기
        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/workSpace")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewWorkSpaceResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewWorkSpaceResponseDTO>>() {
                }
        );

        CrewWorkSpaceResponseDTO responseDTO = response.payload();

        assertThat(responseDTO.projects().size()).isEqualTo(1);
        assertThat(responseDTO.projects().get(0).projectId()).isEqualTo(1L);
        assertThat(responseDTO.projects().get(0).projectStatus()).isEqualTo(ProjectStatus.CONTRACT_PENDING);
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 계약서 작성 후 워크스페이스")
    void workSpaceAfterContracting() throws Exception {
        String token = loginSetting();
        completeProjectApplicationToContract();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/workSpace")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewWorkSpaceResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewWorkSpaceResponseDTO>>() {
                }
        );

        CrewWorkSpaceResponseDTO responseDTO = response.payload();

        assertThat(responseDTO.projects().size()).isEqualTo(1);
        assertThat(responseDTO.projects().get(0).projectId()).isEqualTo(1L);
        assertThat(responseDTO.projects().get(0).projectStatus()).isEqualTo(ProjectStatus.PROGRESS);
    }

    @Test
    @Transactional
    @DisplayName("선정되지 않은 프로젝트의 워크스페이스 진입")
    void workSpaceForUnSelectedProject() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/crews/workSpace/2")
                        .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value("P001"));
    }

    @Test
    @Transactional
    @DisplayName("계약서가 작성되지 않은 프로젝트의 워크스페이스 진입")
    void workSpaceForUnContractedProject() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        //프로젝트 지원하기
        MvcResult applicationMvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> applicationResponse = objectMapper.readValue(
                applicationMvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {
                }
        );

        long applicationId = applicationResponse.payload().applicationId();

        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        //선정하기
        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        //워크스페이스
        mockMvc.perform(get("/api/v1/crews/workSpace/1")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value("P004"));
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 결과물 올리기")
    void uploadProjectResult() throws Exception {
        String token = loginSetting();
        completeProjectApplicationToContract();

        String subject = "제목제목";
        String content = "XXX한 점에 집중하려했습니다.";
        SubmitProjectResultRequestDTO req = new SubmitProjectResultRequestDTO(null, null, subject, content);

        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/workSpace")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewWorkSpaceResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewWorkSpaceResponseDTO>>() {
                }
        );

        CrewWorkSpaceResponseDTO responseDTO = response.payload();

        assertThat(responseDTO.projects().size()).isEqualTo(1);
        assertThat(responseDTO.projects().get(0).projectId()).isEqualTo(1L);
        assertThat(responseDTO.projects().get(0).projectStatus()).isEqualTo(ProjectStatus.INSPECTION);
    }

    @Test
    @Transactional
    @DisplayName("초기 크루 프로젝트 현황은 비어 있다")
    void initialCrewProjectStatus() throws Exception {
        String crewToken = loginSetting();

        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.content.length()")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.payload.number")
                                .value(0)
                );
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 현황을 지원일 기준으로 정렬하고 페이지 조회한다")
    void getCrewProjectsWithPagingAndSort()
            throws Exception {

        String crewToken = loginSetting();

        long firstApplicationId =
                applyProjectAndGetApplicationId(
                        crewToken,
                        1L
                );

        long secondApplicationId =
                applyProjectAndGetApplicationId(
                        crewToken,
                        2L
                );

        /*
         * 최근 지원순: 두 번째 지원이 먼저 조회
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "status",
                                        "APPLIED"
                                )
                                .param(
                                        "sort",
                                        "RECENT"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.content.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.payload.totalPages")
                                .value(2)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].applicationId"
                        ).value(secondApplicationId)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].status"
                        ).value("APPLIED")
                );

        /*
         * 오래된 지원순: 첫 번째 지원이 먼저 조회
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "status",
                                        "APPLIED"
                                )
                                .param(
                                        "sort",
                                        "OLDEST"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].applicationId"
                        ).value(firstApplicationId)
                );
    }

    @Test
    @Transactional
    @DisplayName("프로젝트에 선정되면 지원 상태에서 진행 중 상태로 이동한다")
    void crewProjectStatusChangesAfterSelection()
            throws Exception {

        String crewToken = loginSetting();

        long applicationId =
                applyProjectAndGetApplicationId(
                        crewToken,
                        1L
                );

        String companyToken =
                loginSetting_Company();

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/projects/1/applications/{applicationId}/select",
                                applicationId
                        )
                                .header(
                                        "Authorization",
                                        companyToken
                                )
                )
                .andExpect(status().isOk());

        /*
         * PENDING 지원이 아니므로 APPLIED 목록에서는 제외
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "status",
                                        "APPLIED"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(0)
                );

        /*
         * CONTRACT_PENDING 상태이므로 IN_PROGRESS
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "status",
                                        "IN_PROGRESS"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].applicationId"
                        ).value(applicationId)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].projectId"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].status"
                        ).value("IN_PROGRESS")
                );
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트 현황을 검색어와 조건으로 필터링한다")
    void filterCrewProjects() throws Exception {
        String crewToken = loginSetting();

        applyProjectAndGetApplicationId(
                crewToken,
                1L
        );

        applyProjectAndGetApplicationId(
                crewToken,
                2L
        );

        /*
         * 검색어 필터
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "keyword",
                                        "코카콜라"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                );

        /*
         * 기업 카테고리 필터
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "category",
                                        "IT"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                );

        /*
         * 프로젝트 유형 필터
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "projectType",
                                        "APPTEST"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(2)
                );

        /*
         * 매우 넓은 프로젝트 기간
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "startDate",
                                        "2000-01-01"
                                )
                                .param(
                                        "endDate",
                                        "2100-12-31"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(2)
                );

        /*
         * 프로젝트 시작일이 2100년 이후인 데이터는 없음
         */
        mockMvc.perform(
                        get("/api/v1/crews/projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "startDate",
                                        "2100-01-01"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(0)
                );
    }

    @Test
    @Transactional
    @DisplayName("초기 크루 Todo 목록은 비어 있다")
    void initialCrewTodoProjects() throws Exception {
        String crewToken = loginSetting();

        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.content.length()")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.payload.number")
                                .value(0)
                );
    }

    @Test
    @Transactional
    @DisplayName("Todo의 생성과 완료 상태를 검색, 필터, 정렬하여 조회한다")
    void getCrewTodoProjectsWithFilterAndPaging()
            throws Exception {

        String crewToken = loginSetting();
        String companyToken = loginSetting_Company();

        /*
         * 프로젝트 지원 → 크루 선정 → 계약 완료
         * 프로젝트 상태: PROGRESS
         */
        completeProjectApplicationToContract();

        SubmitProjectResultRequestDTO submissionRequest =
                new SubmitProjectResultRequestDTO(
                        List.of(
                                "result-file-1",
                                "result-file-2"
                        ),
                        "프로젝트 결과물입니다."
                );

        /*
         * 1. 최초 결과물 제출
         * PROGRESS → INSPECTION
         */
        mockMvc.perform(
                        post(
                                "/api/v1/crews/projects/1/submissions"
                        )
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                submissionRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 2. 기업 수정 요청
         * REVISION_SUBMISSION Todo 생성
         * NEEDS_CONFIRMATION
         */

        long firstSubmissionId =
                getLatestSubmissionId(
                        companyToken,
                        1L
                );

        registerFeedback(companyToken, 1L, firstSubmissionId);

        /*
         * 확인 필요 Todo 조회
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "progressStatus",
                                        "NEEDS_CONFIRMATION"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].projectId"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].taskName"
                        ).value("정산 정보 확인")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].progressStatus"
                        ).value("NEEDS_CONFIRMATION")
                );

        /*
         * 작업명 검색
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "keyword",
                                        "정산"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].taskName"
                        ).value("정산 정보 확인")
                );

        /*
         * 정산 Todo 검색
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "keyword",
                                        "정산"
                                )
                                .param(
                                        "progressStatus",
                                        "NEEDS_CONFIRMATION"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].taskName"
                        ).value("정산 정보 확인")
                );

        /*
         * 최신 흐름의 Todo는 정산 정보 확인 1개
         * 최신순 첫 번째는 정산 정보 확인
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "sort",
                                        "RECENT"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.payload.totalPages")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.payload.content.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].taskName"
                        ).value("정산 정보 확인")
                );

        /*
         * 오래된 순 첫 번째도 정산 정보 확인
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "sort",
                                        "OLDEST"
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].taskName"
                        ).value("정산 정보 확인")
                );

        /*
         * 5. 생성된 정산 정보 조회
         */
        MvcResult settlementMvcResult =
                mockMvc.perform(
                                get(
                                        "/api/v1/companies/me/settlements"
                                )
                                        .header(
                                                "Authorization",
                                                companyToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<List<CompanySettlementResponse>>
                settlementResponse =
                objectMapper.readValue(
                        settlementMvcResult.getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<
                                        List<CompanySettlementResponse>
                                        >
                                >() {
                        }
                );

        CompanySettlementResponse settlement =
                settlementResponse.payload()
                        .stream()
                        .filter(response ->
                                response.projectId() == 1L
                        )
                        .findFirst()
                        .orElseThrow();

        /*
         * 6. 정산 완료
         * SETTLEMENT_CONFIRMATION Todo 완료
         */
        CompanySettlementCompleteRequest completeRequest =
                new CompanySettlementCompleteRequest(
                        LocalDate.of(
                                2026,
                                7,
                                15
                        )
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlement.settlementId()
                        )
                                .header(
                                        "Authorization",
                                        companyToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                completeRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 정산 완료 후 정산 정보 확인 Todo가 완료된다.
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "progressStatus",
                                        "COMPLETED"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].progressStatus"
                        ).value("COMPLETED")
                );

        /*
         * 확인 필요 Todo는 더 이상 없음
         */
        mockMvc.perform(
                        get("/api/v1/crews/todo-projects")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "progressStatus",
                                        "NEEDS_CONFIRMATION"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.payload.totalElements")
                                .value(0)
                );
    }

    @Test
    @Transactional
    @DisplayName("초기 크루 정산 요약은 모두 0이다")
    void initialCrewSettlementSummary()
            throws Exception {

        String crewToken =
                loginSetting();

        CrewSettlementSummaryResponse summary =
                getCrewSettlementSummary(
                        crewToken
                );

        assertThat(
                summary.totalPaidAmount()
        ).isEqualTo(0);

        assertThat(
                summary.waitingAmount()
        ).isEqualTo(0);

        assertThat(
                summary.monthlyPaidAmount()
        ).isEqualTo(0);

        assertThat(
                summary.nextExpectedPaymentDate()
        ).isNull();
    }

    @Test
    @Transactional
    @DisplayName("정산 생성과 지급 완료에 따라 크루 정산 요약이 변경된다")
    void crewSettlementSummaryChangesAfterPayment()
            throws Exception {

        String crewToken =
                loginSetting();

        String companyToken =
                loginSetting_Company();

        /*
         * 프로젝트 지원 → 선정 → 계약 완료
         * ProjectStatus.PROGRESS
         */
        completeProjectApplicationToContract();

        SubmitProjectResultRequestDTO submissionRequest =
                new SubmitProjectResultRequestDTO(
                        List.of(
                                "result-file"
                        ),
                        "프로젝트 결과물입니다."
                );

        /*
         * 크루 결과물 제출
         * PROGRESS → INSPECTION
         */
        mockMvc.perform(
                        post(
                                "/api/v1/crews/projects/1/submissions"
                        )
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                submissionRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 기업 결과물 승인
         * INSPECTION → ADJUSTING
         * WAITING 정산 생성
         */
        long submissionId =
                getLatestSubmissionId(
                        companyToken,
                        1L
                );

        registerFeedback(companyToken, 1L, submissionId);

        /*
         * 생성된 정산 조회
         */
        MvcResult settlementMvcResult =
                mockMvc.perform(
                                get(
                                        "/api/v1/companies/me/settlements"
                                )
                                        .header(
                                                "Authorization",
                                                companyToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<List<CompanySettlementResponse>>
                settlementResponse =
                objectMapper.readValue(
                        settlementMvcResult
                                .getResponse()
                                .getContentAsString(),
                        new TypeReference<
                                ApiResponse<
                                        List<CompanySettlementResponse>
                                        >
                                >() {
                        }
                );

        CompanySettlementResponse settlement =
                settlementResponse.payload()
                        .stream()
                        .filter(response ->
                                response.projectId() == 1L
                        )
                        .findFirst()
                        .orElseThrow();

        LocalDate expectedPaymentDate =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                ).plusDays(7);

        CompanySettlementExpectedPaymentDateRequest
                expectedPaymentDateRequest =
                new CompanySettlementExpectedPaymentDateRequest(
                        expectedPaymentDate
                );

        /*
         * 예상 지급일 설정
         */
        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/expected-payment-date",
                                settlement.settlementId()
                        )
                                .header(
                                        "Authorization",
                                        companyToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                expectedPaymentDateRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 지급 대기 상태 요약
         */
        CrewSettlementSummaryResponse waitingSummary =
                getCrewSettlementSummary(
                        crewToken
                );

        assertThat(
                waitingSummary.totalPaidAmount()
        ).isEqualTo(0);

        assertThat(
                waitingSummary.waitingAmount()
        ).isEqualTo(
                settlement.amount()
        );

        assertThat(
                waitingSummary.monthlyPaidAmount()
        ).isEqualTo(0);

        assertThat(
                waitingSummary.nextExpectedPaymentDate()
        ).isEqualTo(
                expectedPaymentDate
        );

        LocalDate settlementDate =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        CompanySettlementCompleteRequest completeRequest =
                new CompanySettlementCompleteRequest(
                        settlementDate
                );

        /*
         * 정산 지급 완료
         * WAITING → PAID
         */
        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlement.settlementId()
                        )
                                .header(
                                        "Authorization",
                                        companyToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                completeRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 지급 완료 상태 요약
         */
        CrewSettlementSummaryResponse paidSummary =
                getCrewSettlementSummary(
                        crewToken
                );

        assertThat(
                paidSummary.totalPaidAmount()
        ).isEqualTo(
                settlement.amount()
        );

        assertThat(
                paidSummary.waitingAmount()
        ).isEqualTo(0);

        assertThat(
                paidSummary.monthlyPaidAmount()
        ).isEqualTo(
                settlement.amount()
        );

        assertThat(
                paidSummary.nextExpectedPaymentDate()
        ).isNull();
    }

    @Test
    @Transactional
    @DisplayName("초기 크루 정산 목록은 비어 있다")
    void initialCrewSettlements()
            throws Exception {

        String crewToken =
                loginSetting();

        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.content.length()"
                        ).value(0)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(0)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.number"
                        ).value(0)
                );
    }

    @Test
    @Transactional
    @DisplayName("크루 정산 목록을 검색하고 조건별로 필터링한다")
    void filterCrewSettlements()
            throws Exception {

        String crewToken =
                loginSetting();

        String companyToken =
                loginSetting_Company();

        CompanySettlementResponse settlement =
                createWaitingSettlement(
                        crewToken,
                        companyToken
                );

        /*
         * 전체 목록
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].settlementId"
                        ).value(
                                settlement.settlementId()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].projectId"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].brandName"
                        ).value("네이버")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].settlementStatus"
                        ).value("WAITING")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].amount"
                        ).value(
                                settlement.amount()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].settlementDate"
                        ).doesNotExist()
                );

        /*
         * 브랜드명 검색
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "keyword",
                                        "네이버"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                );

        /*
         * 존재하지 않는 검색어
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "keyword",
                                        "존재하지않는검색어"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(0)
                );

        /*
         * 지급 대기 상태
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "settlementStatus",
                                        "WAITING"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                );

        /*
         * 아직 지급 완료 정산은 없음
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "settlementStatus",
                                        "PAID"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(0)
                );

        /*
         * 기업 산업 분야
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "category",
                                        "IT"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                );

        /*
         * 프로젝트 유형
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "projectType",
                                        "APPTEST"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                );

        /*
         * 프로젝트 수행 기간과 겹치는 넓은 기간
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "startDate",
                                        "2000-01-01"
                                )
                                .param(
                                        "endDate",
                                        "2100-12-31"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                );

        /*
         * 프로젝트 기간과 겹치지 않는 미래 기간
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "startDate",
                                        "2100-01-01"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(0)
                );

        /*
         * 페이지 정보
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                                .param(
                                        "sort",
                                        "RECENT"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.totalPages"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content.length()"
                        ).value(1)
                );

        /*
         * 기업이 정산 지급 완료
         */
        LocalDate settlementDate =
                LocalDate.of(
                        2026,
                        7,
                        15
                );

        CompanySettlementCompleteRequest completeRequest =
                new CompanySettlementCompleteRequest(
                        settlementDate
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/settlements/{settlementId}/complete",
                                settlement.settlementId()
                        )
                                .header(
                                        "Authorization",
                                        companyToken
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                completeRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        /*
         * 지급 완료 상태 조회
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "settlementStatus",
                                        "PAID"
                                )
                                .param(
                                        "sort",
                                        "OLDEST"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].settlementStatus"
                        ).value("PAID")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.content[0].settlementDate"
                        ).value(
                                settlementDate.toString()
                        )
                );

        /*
         * 지급 대기 상태에서는 제외
         */
        mockMvc.perform(
                        get("/api/v1/crews/settlements")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                                .param(
                                        "settlementStatus",
                                        "WAITING"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.payload.totalElements"
                        ).value(0)
                );
    }
}

//프로젝트들 보기 -> 프로젝트 상세보기 -> 지원하기 -> 결과물올리기 ->
// 검수 후 결과물 올리기 -> 평가 점수 변동 확인 -> 정보수정 -> 포트폴리오

// * 평가점수 변동 확인
// * 포트폴리오 관련 기능