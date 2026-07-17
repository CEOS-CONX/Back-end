package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrewProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    String loginSetting() throws Exception {
        LoginRequestDTO request =
                new LoginRequestDTO(
                        "kimdoes2143@naver.com",
                        "1q2w3e4r!!"
                );

        MvcResult mvcResult =
                mockMvc.perform(
                                post("/api/v1/login")
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

        return mvcResult
                .getResponse()
                .getHeader("Authorization");
    }

    @Test
    @Transactional
    @DisplayName("크루 프로필을 조회한다")
    void getCrewPersonalInformation() throws Exception {
        // given
        String crewToken = loginSetting();

        // when
        MvcResult mvcResult =
                mockMvc.perform(
                                get("/api/v1/crews/me")
                                        .header(
                                                "Authorization",
                                                crewToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<CrewProfileResponse> response =
                objectMapper.readValue(
                        mvcResult
                                .getResponse()
                                .getContentAsString(),
                        new TypeReference<>() {
                        }
                );

        CrewProfileResponse responseDTO =
                response.payload();

        // then
        assertThat(responseDTO.crewId())
                .isEqualTo(1L);

        assertThat(responseDTO.email())
                .isEqualTo(
                        "kimdoes2143@naver.com"
                );

        assertThat(responseDTO.schools())
                .isEmpty();

        assertThat(responseDTO.links())
                .isNotNull();

        assertThat(responseDTO.files())
                .isNotNull();

        assertThat(responseDTO.portfolios())
                .isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("크루 프로필을 수정한다")
    void modifyCrewPersonalInformation() throws Exception {
        // given
        String crewToken = loginSetting();

        CrewProfileUpdateRequest request =
                new CrewProfileUpdateRequest(
                        "$$$",
                        "크루이름",
                        CrewType.CLUB,
                        null,
                        "오정민",
                        "회장",
                        "창업",
                        Industry.CAREER,
                        12,
                        "아이디어를 현실로 만드는 크루",
                        "저희는 재미있게 만들어가는 동아리입니다.",
                        List.of(
                                "홍익대학교"
                        ),
                        List.of(
                                "기획",
                                "콘텐츠 제작"
                        ),
                        List.of(
                                "마케팅",
                                "SNS 운영"
                        ),
                        List.of(),
                        List.of()
                );

        // when
        mockMvc.perform(
                        patch("/api/v1/crews/me")
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
                .andExpect(status().isOk());

        MvcResult mvcResult =
                mockMvc.perform(
                                get("/api/v1/crews/me")
                                        .header(
                                                "Authorization",
                                                crewToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        ApiResponse<CrewProfileResponse> response =
                objectMapper.readValue(
                        mvcResult
                                .getResponse()
                                .getContentAsString(),
                        new TypeReference<>() {
                        }
                );

        CrewProfileResponse responseDTO =
                response.payload();

        // then
        assertThat(responseDTO.crewId())
                .isEqualTo(1L);

        assertThat(responseDTO.email())
                .isEqualTo(
                        "kimdoes2143@naver.com"
                );

        assertThat(responseDTO.profileImage())
                .isEqualTo("$$$");

        assertThat(responseDTO.crewName())
                .isEqualTo("크루이름");

        assertThat(responseDTO.activityField())
                .isEqualTo("창업");

        assertThat(responseDTO.interestingIndustry())
                .isEqualTo(Industry.CAREER);

        assertThat(responseDTO.memberAmount())
                .isEqualTo(12);

        assertThat(responseDTO.catchphrase())
                .isEqualTo(
                        "아이디어를 현실로 만드는 크루"
                );

        assertThat(responseDTO.crewIntroduction())
                .isEqualTo(
                        "저희는 재미있게 만들어가는 동아리입니다."
                );

        assertThat(responseDTO.schools())
                .containsExactly(
                        "홍익대학교"
                );

        assertThat(responseDTO.advantages())
                .containsExactly(
                        "기획",
                        "콘텐츠 제작"
                );

        assertThat(responseDTO.specialties())
                .containsExactly(
                        "마케팅",
                        "SNS 운영"
                );
    }

    @Test
    @Transactional
    @DisplayName("프로젝트를 북마크한다")
    void bookmarkProject() throws Exception {
        // given
        String crewToken = loginSetting();

        // when
        mockMvc.perform(
                        post("/api/v1/projects/2/bookmarks")
                                .header(
                                        "Authorization",
                                        crewToken
                                )
                )
                .andExpect(status().isOk());

        MvcResult mvcResult =
                mockMvc.perform(
                                get(
                                        "/api/v1/crews/me/bookmarked-projects"
                                )
                                        .header(
                                                "Authorization",
                                                crewToken
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode root =
                objectMapper.readTree(
                        mvcResult
                                .getResponse()
                                .getContentAsString()
                );

        JsonNode content =
                root.path("payload")
                        .path("content");

        // then
        assertThat(content.size())
                .isEqualTo(1);

        assertThat(
                content.get(0)
                        .path("projectId")
                        .asLong()
        ).isEqualTo(2L);
    }
}