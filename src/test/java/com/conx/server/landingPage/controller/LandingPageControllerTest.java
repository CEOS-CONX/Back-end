package com.conx.server.landingPage.controller;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.landingPage.dto.AnonymousLandingPageResponseDTO;
import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.landingPage.service.AnonymousLandingPageService;
import com.conx.server.landingPage.service.CompanyLandingPageService;
import com.conx.server.landingPage.service.CrewLandingPageService;
import com.conx.server.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LandingPageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompanyLandingPageService companyLandingService;

    @Mock
    private CrewLandingPageService crewLandingPage;

    @Mock
    private AnonymousLandingPageService anonymousLandingPageService;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory = new ApiResponseFactory(notificationRepository);

        LandingPageController landingPageController = new LandingPageController(
                companyLandingService,
                crewLandingPage,
                anonymousLandingPageService,
                apiResponseFactory
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(landingPageController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("비로그인 사용자의 랜딩페이지를 조회한다")
    void landingAnonymous() throws Exception {
        // given
        ProjectWrapperForLandingPageDTO project = new ProjectWrapperForLandingPageDTO(
                1L,
                List.of("project-image.png"),
                "테스트 프로젝트",
                "테스트 기업",
                null,
                null,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        );

        CrewWrapperForLandingPageDTO crew = new CrewWrapperForLandingPageDTO(
                1L,
                "crew-image.png",
                "테스트 크루",
                "테스트 소개",
                null,
                null,
                4.5,
                3
        );

        AnonymousLandingPageResponseDTO response = new AnonymousLandingPageResponseDTO(
                List.of(project),
                List.of(crew)
        );

        given(anonymousLandingPageService.landing()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/landing"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
                .andExpect(jsonPath("$.payload.projects[0].projectId").value(1))
                .andExpect(jsonPath("$.payload.projects[0].projectName").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.payload.projects[0].companyName").value("테스트 기업"))
                .andExpect(jsonPath("$.payload.crews[0].crewId").value(1))
                .andExpect(jsonPath("$.payload.crews[0].crewName").value("테스트 크루"))
                .andExpect(jsonPath("$.payload.crews[0].point").value(4.5))
                .andExpect(jsonPath("$.payload.crews[0].totalProject").value(3));

        verify(anonymousLandingPageService).landing();
    }
}