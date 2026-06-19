package com.conx.server;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.dto.response.ProjectBrowseDetailResponse;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConxServerApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProjectRepository projectRepository;

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
    String loginSetting_Company() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("navernaver@gmail.com", "1q2w3e4r!!");
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        return mvcResult.getResponse().getHeader("Authorization");
    }

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
                new TypeReference<ApiResponse<LoginResponseDTO>>() {}
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
                new TypeReference<ApiResponse<List<ProjectWrapperForLandingPageDTO>>>() {}
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

    //여기 아직 테스트 통과를 안해서 CI가 안될 수 있습니다..!
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
                .andExpect(jsonPath("$.payload.content.length()").value(1));
    }

    //여기 아직 테스트 통과를 안해서 CI가 안될 수 있습니다..!
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
                .andExpect(jsonPath("$.payload.content.length()").value(2));
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
                new TypeReference<ApiResponse<ProjectBrowseDetailResponse>>() {}
        );

        ProjectBrowseDetailResponse detailResponse = response.payload();
        assertThat(detailResponse.projectId()).isEqualTo(1);
        assertThat(detailResponse.brandName()).isEqualTo("네이버");
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 지원하기")
    void applyProject() throws Exception {
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용", "no후회ㄱㄱㄱ");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {}
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

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용", "no후회ㄱㄱㄱ");

        mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        //프로젝트 크루 선정
        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/1/select")
                .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        //재확인
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/applications?status=ALL")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewApplicationStatusResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {}
        );

        Project project = projectRepository.findById(1L).get();

        CrewApplicationStatusResponseDTO applicationResponse = response.payload();
        assertThat(applicationResponse.applications().get(0).applicationId()).isEqualTo(1);
        assertThat(applicationResponse.applications().get(0).status()).isEqualTo(ProjectApplicationStatus.SELECTED);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.CONTRACT_PENDING);
        assertThat(response.hasNotification()).isEqualTo(true);
    }
}

//프로젝트들 보기 -> 프로젝트 상세보기 -> 지원하기 -> 결과물올리기 -> 검수 후 결과물 올리기 -> 평가 점수 변동 확인 -> 정보수정 -> 포트폴리오