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
import com.conx.server.user.repository.AdminRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminWorkSpaceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AdminRepository adminRepository;

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
    @DisplayName("어드민_검수하기")
    void completeContractInAdmin() throws Exception {
        //프로젝트 크루 지원 및 선정
        String token = loginSetting();

        ProjectApplicationRequest req = new ProjectApplicationRequest("안녕하세용, no후회ㄱㄱㄱ");

        mockMvc.perform(post("/api/v1/projects/1/applications")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/1/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        //어드민 검수 시작
        String token_admin = loginSetting_Admin();

        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", token_admin))
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
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.PROGRESS);
        assertThat(response.hasNotification()).isEqualTo(true);
    }

    @Test
    @Transactional
    @DisplayName("계약서 작성 중인 상태가 아닌 프로젝트의 어드민 접근 시 오류")
    void completeContractInvalidProjectStatus() throws Exception {
        String token_admin = loginSetting_Admin();

        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", token_admin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value("CR002"));

        //계약서 작성 완료 후 계약서 작성 완료 상태변환 시 오류
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
                new TypeReference<ApiResponse<ProjectApplicationResponse>>() {}
        );

        long applicationId = response.payload().applicationId();

        String tokenCompany = loginSetting_Company();
        mockMvc.perform(post("/api/v1/companies/me/projects/1/applications/" + applicationId + "/select")
                        .header("Authorization", tokenCompany))
                .andExpect(status().isOk());

        //어드민 검수 시작
        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", token_admin))
                .andExpect(status().isOk());

        //어드민 검수 후 또 검수하려면 에러
        mockMvc.perform(patch("/api/v1/admin/projects/1/contract-complete")
                        .header("Authorization", token_admin))
                .andExpect(jsonPath("$.status")
                        .value("P002"));
    }
}
