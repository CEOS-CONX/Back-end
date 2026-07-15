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
import com.conx.server.user.dto.company.request.CompanyProjectRevisionRequest;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.*;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginResponseDTO;
import com.conx.server.user.repository.AdminRepository;
import com.conx.server.user.repository.CrewRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

        assertThat(landingResponse.size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("크루 프로젝트들 보기")
    void browseProjects() throws Exception {
        String token = loginSetting();

        mockMvc.perform(get("/api/v1/projects?page=0&size=6")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.content.length()").value(2));
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
                .andExpect(jsonPath("$.payload.content.length()").value(1));
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
                .andExpect(jsonPath("$.payload.content.length()").value(2));
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
                .andExpect(jsonPath("$.payload.content.length()").value(2));
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

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.doneProjectAmount()).isEqualTo(0);
        assertThat(resultDTO.todoProjects().size()).isEqualTo(0);
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

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(0);
        assertThat(projectInfoDTO.doneProjectAmount()).isEqualTo(0);
        assertThat(resultDTO.todoProjects().size()).isEqualTo(0);
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

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.doneProjectAmount()).isEqualTo(0);
        assertThat(resultDTO.todoProjects().size()).isEqualTo(0);
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

        assertThat(projectInfoDTO.appliedProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.progressProjectAmount()).isEqualTo(1);
        assertThat(projectInfoDTO.doneProjectAmount()).isEqualTo(0);
        assertThat(resultDTO.todoProjects().size()).isEqualTo(0);
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
}
/*
    @Test
    @Transactional
    @DisplayName("검수 후 결과물 올리기")
    void uploadProjectResultAfterInspection() throws Exception {
        String crewToken = loginSetting();
        String companyToken = loginSetting_Company();
        completeProjectApplicationToContract();

        String subject = "제목제목";
        String content = "XXX한 점에 집중하려했습니다.";
        SubmitProjectResultRequestDTO req = new SubmitProjectResultRequestDTO(null, null, subject, content);

        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String revision = "~~ 여기 좀 수정해주세용ㅇㅇ";
        CompanyProjectRevisionRequest rivReq = new CompanyProjectRevisionRequest(revision);
        mockMvc.perform(post("/api/v1/companies/me/projects/1/revision-requests")
                .header("Authorization", companyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rivReq)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/workSpace")
                        .header("Authorization", crewToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewWorkSpaceResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewWorkSpaceResponseDTO>>() {}
        );

        CrewWorkSpaceResponseDTO responseDTO = response.payload();

        assertThat(responseDTO.projects().size()).isEqualTo(1);
        assertThat(responseDTO.projects().get(0).projectId()).isEqualTo(1L);
        assertThat(responseDTO.projects().get(0).projectStatus()).isEqualTo(ProjectStatus.INSPECTION);
    }

    @Test
    @Transactional
    @DisplayName("수정사항이 도착하지 않았는데 결과물 재업로드")
    void uploadProjectResultBeforeInspection() throws Exception {
        String crewToken = loginSetting();
        String companyToken = loginSetting_Company();
        completeProjectApplicationToContract();

        String subject = "제목제목";
        String content = "XXX한 점에 집중하려했습니다.";
        SubmitProjectResultRequestDTO req = new SubmitProjectResultRequestDTO(null, null, subject, content);



        //결과물 제출
        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();


        //수정된 결과물 재제출
        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value("P002"));
    }

 */

    /*
    @Test
    @Transactional
    @DisplayName("첫 번째 검수 후 업로드")
    void uploadProjectResultAfterInspectionAndUpload() throws Exception {
        String crewToken = loginSetting();
        String companyToken = loginSetting_Company();
        completeProjectApplicationToContract();

        String subject = "제목제목";
        String content = "XXX한 점에 집중하려했습니다.";
        SubmitProjectResultRequestDTO req = new SubmitProjectResultRequestDTO(null, null, subject, content);



        //결과물 제출
        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        //결과물 수정요청
        String revision = "~~ 여기 좀 수정해주세용ㅇㅇ";
        CompanyProjectRevisionRequest rivReq = new CompanyProjectRevisionRequest(revision);
        mockMvc.perform(post("/api/v1/companies/me/projects/1/revision-requests")
                        .header("Authorization", companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rivReq)))
                .andExpect(status().isOk());

        //수정된 결과물 제출
        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        //수정된 결과물 재제출
        mockMvc.perform(post("/api/v1/crews/projects/1/submissions")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value("P002"));
    }
}

     */

//프로젝트들 보기 -> 프로젝트 상세보기 -> 지원하기 -> 결과물올리기 ->
// 검수 후 결과물 올리기 -> 평가 점수 변동 확인 -> 정보수정 -> 포트폴리오

// * 평가점수 변동 확인
// * 포트폴리오 관련 기능