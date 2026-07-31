package com.conx.server.user.controller.admin;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class AdminWorkSpaceTest {

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

    @Test
    @Transactional
    @DisplayName("기존 계약 대기 프로젝트를 어드민이 진행중으로 전환한다")
    void completeLegacyContractPendingProjectInAdmin() throws Exception {
        // 크루 로그인
        String crewToken = loginSetting();

        // 프로젝트 지원
        ProjectApplicationRequest req =
                new ProjectApplicationRequest("안녕하세용, no후회ㄱㄱㄱ");

        MvcResult applicationResult = mockMvc.perform(
                        post("/api/v1/projects/1/applications")
                                .header("Authorization", crewToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> applicationResponse =
                objectMapper.readValue(
                        applicationResult.getResponse().getContentAsString(),
                        new TypeReference<ApiResponse<ProjectApplicationResponse>>() {}
                );

        long applicationId = applicationResponse.payload().applicationId();

        // 기업 로그인 후 지원자 선정
        String companyToken = loginSetting_Company();

        mockMvc.perform(
                        post("/api/v1/companies/me/projects/1/applications/{applicationId}/select",
                                applicationId)
                                .header("Authorization", companyToken))
                .andExpect(status().isOk());

        /*
         * 신규 선정 프로젝트는 바로 PROGRESS가 된다.
         * 배포 전에 저장된 기존 CONTRACT_PENDING 데이터를 테스트에서만 재현한다.
         */
        Project legacyProject = projectRepository.findById(1L)
                .orElseThrow();

        ReflectionTestUtils.setField(
                legacyProject,
                "status",
                ProjectStatus.CONTRACT_PENDING
        );
        projectRepository.saveAndFlush(legacyProject);

        // 관리자 로그인 후 기존 계약 대기 프로젝트를 진행중으로 전환
        String adminToken = loginSetting_Admin();

        mockMvc.perform(
                        patch("/api/v1/admin/projects/1/contract-complete")
                                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        // 결과 확인
        MvcResult result = mockMvc.perform(
                        get("/api/v1/crews/applications")
                                .param("status", "ALL")
                                .header("Authorization", crewToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewApplicationStatusResponseDTO> response =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        new TypeReference<ApiResponse<CrewApplicationStatusResponseDTO>>() {}
                );

        Project project = projectRepository.findById(1L)
                .orElseThrow();

        CrewApplicationStatusResponseDTO dto = response.payload();

        assertThat(dto.applications()).hasSize(1);
        assertThat(dto.applications().get(0).applicationId()).isEqualTo(applicationId);
        assertThat(dto.applications().get(0).status()).isEqualTo(ProjectApplicationStatus.SELECTED);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.PROGRESS);
        assertThat(project.getPreviousStatus()).isEqualTo(ProjectStatus.PROGRESS);
        assertThat(response.hasNotification()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("계약 완료 처리할 수 없는 프로젝트의 어드민 접근 시 오류")
    void completeContractInvalidProjectStatus() throws Exception {
        String adminToken = loginSetting_Admin();

        /*
         * 선정된 크루가 없는 프로젝트는 계약 완료 처리할 수 없다.
         */
        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value("CR002"));

        /*
         * 신규 흐름에서는 선정과 동시에 PROGRESS로 전환된다.
         */
        String crewToken = loginSetting();

        ProjectApplicationRequest req =
                new ProjectApplicationRequest("안녕하세용 no후회ㄱㄱㄱ");

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/projects/1/applications")
                                .header("Authorization", crewToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectApplicationResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {}
        );

        long applicationId = response.payload().applicationId();

        String companyToken = loginSetting_Company();

        mockMvc.perform(
                        post("/api/v1/companies/me/projects/1/applications/{applicationId}/select",
                                applicationId)
                                .header("Authorization", companyToken))
                .andExpect(status().isOk());

        Project project = projectRepository.findById(1L)
                .orElseThrow();

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.PROGRESS);
        assertThat(project.getPreviousStatus()).isEqualTo(ProjectStatus.PROGRESS);

        /*
         * 이미 PROGRESS인 신규 선정 프로젝트에는 계약 완료 API를 호출할 수 없다.
         */
        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", adminToken))
                .andExpect(jsonPath("$.status")
                        .value("P002"));
    }
}
