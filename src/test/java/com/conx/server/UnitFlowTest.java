package com.conx.server;

import static org.assertj.core.api.Assertions.assertThat;
import com.conx.server.global.common.ApiResponse;
import com.conx.server.notification.domain.NotificationType;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.project.dto.request.ProjectApplicationRequest;
import com.conx.server.project.dto.response.CrewInfoForProjectApplicationDTO;
import com.conx.server.project.dto.response.ProjectApplicationResponse;
import com.conx.server.user.dto.company.response.CompanyWorkspaceProjectDetailResponse;
import com.conx.server.user.dto.company.response.ProjectApplicationForCompanyWrapperDTO;
import com.conx.server.user.dto.company.response.ProjectStatusResponseDTO;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UnitFlowTest {

    @Transactional
    String loginSettingCrew(String email) throws Exception {
        LoginRequestDTO req = new LoginRequestDTO(email, "1q2w3e4r!!");
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
    String loginSettingCompany(String email) throws Exception {
        LoginRequestDTO req = new LoginRequestDTO(email, "1q2w3e4r!!");
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

    @Test
    @Transactional
    public void unitTest() throws Exception{
        //로그인
        String crew1Token = loginSettingCrew("kimdoes2143@naver.com");
        String crew2Token = loginSettingCrew("testing1234@gmail.com");
        String companyToken = loginSettingCompany("kdhyun422@gmail.com");
        String adminToken = loginSettingAdmin();

        //프로젝트 지원하기 버튼 누름
        MvcResult mvcForProjectPage = mockMvc.perform(get("/api/v1/projects/applications/my-info")
                        .header("Authorization", crew1Token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewInfoForProjectApplicationDTO> resForProjectPage = objectMapper.readValue(
                mvcForProjectPage.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewInfoForProjectApplicationDTO>>() {
                }
        );

        assertThat(resForProjectPage.payload().crewName()).isEqualTo("홍익대학교 서예동아리");
        System.out.println(resForProjectPage);

        //프로젝트 지원하기 버튼 접근2
        MvcResult mvcForProjectPage2 = mockMvc.perform(get("/api/v1/projects/applications/my-info")
                        .header("Authorization", crew2Token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewInfoForProjectApplicationDTO> resForProjectPage2 = objectMapper.readValue(
                mvcForProjectPage2.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewInfoForProjectApplicationDTO>>() {
                }
        );

        assertThat(resForProjectPage2.payload().crewName()).isEqualTo("연세대학교 IT창업동아리");
        System.out.println(resForProjectPage);


        //프로젝트 지원하기
        ProjectApplicationRequest appReq1 = new ProjectApplicationRequest("지원합니다.");
        ProjectApplicationRequest appReq2 = new ProjectApplicationRequest("우리는 최고입니다.");

        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", crew1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appReq1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/projects/2/applications")
                        .header("Authorization", crew2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appReq2)))
                .andExpect(status().isOk());

        //기업의 확인
        MvcResult mvcForCompanyProject1 = mockMvc.perform(get("/api/v1/companies/me/projects/2")
                        .header("Authorization", companyToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CompanyWorkspaceProjectDetailResponse> resForCompanyProject1 = objectMapper.readValue(
                mvcForCompanyProject1.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CompanyWorkspaceProjectDetailResponse>>() {
                }
        );

        ProjectApplicationForCompanyWrapperDTO resForCompanyProjectDTO = (ProjectApplicationForCompanyWrapperDTO) resForCompanyProject1.payload();


        assertThat(resForCompanyProjectDTO.applications().size()).isEqualTo(2);
        assertThat(resForCompanyProjectDTO.applications().get(0).motivation()).isEqualTo(appReq1.motivation());
        System.out.println(resForCompanyProject1);

        //기업의 선정
        mockMvc.perform(post("/api/v1/companies/me/projects/2/applications/1/select")
                        .header("Authorization", companyToken))
                .andExpect(status().isOk());

        //크루별 알림 확인
        MvcResult mvcForNotification1 = mockMvc.perform(get("/api/v1/notifications?filter=ALL")
                        .header("Authorization", crew1Token))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcForNotification2 = mockMvc.perform(get("/api/v1/notifications?filter=ALL")
                        .header("Authorization", crew2Token))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<NotificationWrapperDTO>> resForNotification1 = objectMapper.readValue(
                mvcForNotification1.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<List<NotificationWrapperDTO>>>() {
                }
        );

        ApiResponse<List<NotificationWrapperDTO>> resForNotification2 = objectMapper.readValue(
                mvcForNotification2.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<List<NotificationWrapperDTO>>>() {
                }
        );

        assertThat(resForNotification1.payload().size()).isEqualTo(1);
        assertThat(resForNotification1.payload().get(0).type()).isEqualTo(NotificationType.PROJECT_SELECTED);

        assertThat(resForNotification2.payload().size()).isEqualTo(1);
        assertThat(resForNotification2.payload().get(0).type()).isEqualTo(NotificationType.PROJECT_REJECTED);

        //기업의 확인
        MvcResult mvcForCompanyProject2 = mockMvc.perform(get("/api/v1/companies/me/projects/2")
                        .header("Authorization", companyToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectStatusResponseDTO> resForCompanyProject2 = objectMapper.readValue(
                mvcForCompanyProject2.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectStatusResponseDTO>>() {
                }
        );

        assertThat(resForCompanyProject2.payload().inspections().size()).isEqualTo(0);
        assertThat(resForCompanyProject2.payload().common().crewId()).isEqualTo(1);

        //계약서
        mockMvc.perform(patch("/api/v1/admin/projects/2/contract-complete")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());

        //기업의 확인
        MvcResult mvcForCompanyProject3 = mockMvc.perform(get("/api/v1/companies/me/projects/2")
                        .header("Authorization", companyToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ProjectStatusResponseDTO> resForCompanyProject3 = objectMapper.readValue(
                mvcForCompanyProject3.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ProjectStatusResponseDTO>>() {
                }
        );

        assertThat(resForCompanyProject3.payload().inspections().size()).isEqualTo(0);
        assertThat(resForCompanyProject3.payload().common().crewId()).isEqualTo(1);

        //결과물 업로드

    }

}
