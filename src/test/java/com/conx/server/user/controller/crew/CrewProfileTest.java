package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.request.CrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.request.ModifyCrewPortfolioRequestDTO;
import com.conx.server.user.dto.crew.response.CrewPortfolioResponseDTO;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CrewProfileTest {
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

    @Test
    @Transactional
    @DisplayName("크루 개인정보 가져오기")
    void getCrewPersonalInformation() throws Exception {
        String crewToken = loginSetting();

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/me")
                        .header("Authorization", crewToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewProfileResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewProfileResponse>>() {
                }
        );
        CrewProfileResponse responseDTO = response.payload();

        assertThat(responseDTO.crewId()).isEqualTo(1);
        assertThat(responseDTO.email()).isEqualTo("kimdoes2143@naver.com");
        assertThat(responseDTO.schools()).isEqualTo(Collections.emptyList());
    }

    @Test
    @Transactional
    @DisplayName("크루 개인정보 수정하기")
    void modifyCrewPersonalInformation() throws Exception {
        String crewToken = loginSetting();
        CrewProfileUpdateRequest req = new CrewProfileUpdateRequest(
                "$$$",
                "크루이름",
                CrewType.CLUB,
                null,
                "오정민",
                "회장",

                null,                    // activityField
                Industry.CAREER,         // interestingIndustry

                12,
                "저희는 재밌게 만들어가는 동아리입니다!",
                null,                    // crewIntroduction

                List.of("홍익대학교"),
                null,                    // advantages
                null,                    // specialties

                null,                    // links
                null                     // files
        );

        mockMvc.perform(patch("/api/v1/crews/me")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();


        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/me")
                        .header("Authorization", crewToken))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewProfileResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewProfileResponse>>() {
                }
        );
        CrewProfileResponse responseDTO = response.payload();

        assertThat(responseDTO.crewId()).isEqualTo(1);
        assertThat(responseDTO.email()).isEqualTo("kimdoes2143@naver.com");
        assertThat(responseDTO.schools()).isEqualTo(List.of("홍익대학교"));
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 북마크하기")
    void bookmarkProject() throws Exception {
        String crewToken = loginSetting();

        mockMvc.perform(post("/api/v1/projects/2/bookmarks")
                        .header("Authorization", crewToken))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/crews/me/bookmarked-projects")
                        .header("Authorization", crewToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(
                mvcResult.getResponse().getContentAsString()
        );

        JsonNode content = root.path("payload").path("content");

        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).path("projectId").asLong()).isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("크루 포트폴리오 등록하기")
    void registerPortfolio() throws Exception {
        String crewToken = loginSetting();

        CrewPortfolioRequestDTO mock1 = new CrewPortfolioRequestDTO(
                "https://cdn.example.com/portfolio/img1.png",
                "브랜드 아이덴티티 디자인",
                "https://cdn.example.com/portfolio/file1.pdf"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/crews/me/portfolio")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mock1)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewPortfolioResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewPortfolioResponseDTO>>() {
                }
        );

        CrewPortfolioResponseDTO responseDTO = response.payload();
        assertThat(responseDTO.name()).isEqualTo(mock1.name());
        assertThat(responseDTO.imageLink()).isEqualTo(mock1.imageLink());
        assertThat(responseDTO.fileLink()).isEqualTo(mock1.fileLink());
    }

    @Test
    @Transactional
    @DisplayName("크루 포트폴리오 수정하기")
    void modifyPortfolio() throws Exception {
        String crewToken = loginSetting();

        CrewPortfolioRequestDTO mock1 = new CrewPortfolioRequestDTO(
                "https://cdn.example.com/portfolio/img1.png",
                "브랜드 아이덴티티 디자인",
                "https://cdn.example.com/portfolio/file1.pdf"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/crews/me/portfolio")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mock1)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewPortfolioResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewPortfolioResponseDTO>>() {
                }
        );

        ModifyCrewPortfolioRequestDTO mock2 = new ModifyCrewPortfolioRequestDTO(
                "https://cdn.example.com/portfolio/img2.png",
                "웹사이트 리뉴얼 프로젝트",
                "https://cdn.example.com/portfolio/file2.pdf"
        );

        MvcResult mvcResult2 = mockMvc.perform(patch("/api/v1/crews/me/portfolio/" + response.payload().id())
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mock2)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewPortfolioResponseDTO> portfolioResponseDTO = objectMapper.readValue(
                mvcResult2.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewPortfolioResponseDTO>>() {
                }
        );

        CrewPortfolioResponseDTO responseDTO = portfolioResponseDTO.payload();
        assertThat(responseDTO.name()).isEqualTo(mock2.name());
        assertThat(responseDTO.imageLink()).isEqualTo(mock2.imageLink());
        assertThat(responseDTO.fileLink()).isEqualTo(mock2.fileLink());
    }

    @Test
    @Transactional
    @DisplayName("크루 포트폴리오 삭제하기")
    void deletePortfolio() throws Exception {
        String crewToken = loginSetting();

        CrewPortfolioRequestDTO mock1 = new CrewPortfolioRequestDTO(
                "https://cdn.example.com/portfolio/img1.png",
                "브랜드 아이덴티티 디자인",
                "https://cdn.example.com/portfolio/file1.pdf"
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/crews/me/portfolio")
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mock1)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<CrewPortfolioResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<CrewPortfolioResponseDTO>>() {
                }
        );

        mockMvc.perform(delete("/api/v1/crews/me/portfolio/" + response.payload().id())
                        .header("Authorization", crewToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mock1)))
                .andExpect(status().isOk())
                .andReturn();
    }
}
