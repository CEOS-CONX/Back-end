package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ProjectBrowseSort;
import com.conx.server.project.dto.response.ProjectBrowseResponse;
import com.conx.server.project.service.ProjectBrowseService;
import com.conx.server.user.domain.types.Industry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectBrowseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectBrowseService projectBrowseService;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory = new ApiResponseFactory(notificationRepository);
        ProjectBrowseController projectBrowseController = new ProjectBrowseController(
                projectBrowseService,
                apiResponseFactory
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(projectBrowseController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("프로젝트 목록을 조회한다")
    void getProjects() throws Exception {
        // given
        ProjectBrowseResponse project = new ProjectBrowseResponse(
                false,
                false,
                1L,
                List.of("project-image.png"),
                "테스트 프로젝트",
                "테스트 기업",
                null,
                null,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                100000L,
                true
        );

        Page<ProjectBrowseResponse> response = new PageImpl<>(
                List.of(project),
                PageRequest.of(0, 10),
                1
        );

        given(projectBrowseService.getProjects(
                nullable(String.class),
                nullable(Industry.class),
                nullable(ProjectType.class),
                nullable(LocalDate.class),
                nullable(LocalDate.class),
                nullable(ProjectBrowseSort.class),
                anyInt(),
                anyInt(),
                isNull()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/projects")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.content[0].projectId").value(1))
                .andExpect(jsonPath("$.payload.content[0].projectName").value("테스트 프로젝트"));

        verify(projectBrowseService).getProjects(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(0),
                eq(10),
                isNull()
        );
    }

    @Test
    @DisplayName("날짜 필터를 포함해서 프로젝트 목록을 조회한다")
    void getProjectsWithDateFilter() throws Exception {
        // given
        Page<ProjectBrowseResponse> response = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        given(projectBrowseService.getProjects(
                nullable(String.class),
                nullable(Industry.class),
                nullable(ProjectType.class),
                nullable(LocalDate.class),
                nullable(LocalDate.class),
                nullable(ProjectBrowseSort.class),
                anyInt(),
                anyInt(),
                isNull()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/projects")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-06-30")
                        .param("sort", "RECENT")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 목록 조회에 성공했습니다."));

        verify(projectBrowseService).getProjects(
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2026, 6, 1)),
                eq(LocalDate.of(2026, 6, 30)),
                eq(ProjectBrowseSort.RECENT),
                eq(0),
                eq(10),
                isNull()
        );
    }

    @Test
    @DisplayName("키워드를 포함해서 프로젝트 목록을 조회한다")
    void getProjectsWithKeyword() throws Exception {
        // given
        Page<ProjectBrowseResponse> response = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        given(projectBrowseService.getProjects(
                nullable(String.class),
                nullable(Industry.class),
                nullable(ProjectType.class),
                nullable(LocalDate.class),
                nullable(LocalDate.class),
                nullable(ProjectBrowseSort.class),
                anyInt(),
                anyInt(),
                isNull()
        )).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/projects?keyword=브랜드&page=0&size=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("프로젝트 목록 조회에 성공했습니다."));

        verify(projectBrowseService).getProjects(
                eq("브랜드"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(0),
                eq(10),
                isNull()
        );
    }
}