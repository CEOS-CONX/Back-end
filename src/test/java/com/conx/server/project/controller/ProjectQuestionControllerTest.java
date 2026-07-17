package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.dto.request.ProjectQuestionAnswerRequest;
import com.conx.server.project.dto.request.ProjectQuestionCreateRequest;
import com.conx.server.project.dto.response.ProjectQuestionDetailResponse;
import com.conx.server.project.dto.response.ProjectQuestionResponse;
import com.conx.server.project.service.ProjectQuestionService;
import com.conx.server.user.dto.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectQuestionControllerTest {

    private static final Long PROJECT_ID = 1L;
    private static final Long QUESTION_ID = 10L;
    private static final Long USER_ID = 20L;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProjectQuestionService projectQuestionService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory =
                new ApiResponseFactory(notificationRepository);

        ProjectQuestionController controller =
                new ProjectQuestionController(
                        projectQuestionService,
                        apiResponseFactory
                );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        lenient().when(userDetails.getId()).thenReturn(USER_ID);
        lenient().when(
                notificationRepository.existsByreceiverIdAndIsRead(
                        USER_ID,
                        false
                )
        ).thenReturn(false);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .setCustomArgumentResolvers(
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(
                                    MethodParameter parameter
                            ) {
                                return parameter.hasParameterAnnotation(
                                        AuthenticationPrincipal.class
                                );
                            }

                            @Override
                            public Object resolveArgument(
                                    MethodParameter parameter,
                                    ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest,
                                    WebDataBinderFactory binderFactory
                            ) {
                                return userDetails;
                            }
                        }
                )
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(objectMapper)
                )
                .build();
    }

    @Test
    @DisplayName("프로젝트 질문 상세 내용을 조회한다")
    void getQuestion() throws Exception {
        // given
        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 12, 14, 30);

        ProjectQuestionDetailResponse response =
                new ProjectQuestionDetailResponse(
                        QUESTION_ID,
                        PROJECT_ID,
                        USER_ID,
                        UserRole.CREW,
                        "김크루",
                        "질문 내용입니다.",
                        false,
                        "답변 내용입니다.",
                        createdAt.plusHours(1),
                        createdAt,
                        createdAt.plusHours(1)
                );

        given(
                projectQuestionService.getQuestion(
                        PROJECT_ID,
                        QUESTION_ID,
                        userDetails
                )
        ).willReturn(response);

        // when & then
        mockMvc.perform(
                        get(
                                "/api/v1/projects/{projectId}/questions/{questionId}",
                                PROJECT_ID,
                                QUESTION_ID
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("프로젝트 질문 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.questionId")
                        .value(QUESTION_ID))
                .andExpect(jsonPath("$.payload.content")
                        .value("질문 내용입니다."))
                .andExpect(jsonPath("$.payload.answerContent")
                        .value("답변 내용입니다."));

        verify(projectQuestionService).getQuestion(
                PROJECT_ID,
                QUESTION_ID,
                userDetails
        );
    }

    @Test
    @DisplayName("프로젝트 질문을 작성한다")
    void createQuestion() throws Exception {
        // given
        ProjectQuestionCreateRequest request =
                new ProjectQuestionCreateRequest(
                        "질문 내용입니다.",
                        true
                );

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 12, 14, 30);

        ProjectQuestionDetailResponse response =
                new ProjectQuestionDetailResponse(
                        QUESTION_ID,
                        PROJECT_ID,
                        USER_ID,
                        UserRole.CREW,
                        "김크루",
                        "질문 내용입니다.",
                        true,
                        null,
                        null,
                        createdAt,
                        createdAt
                );

        given(
                projectQuestionService.createQuestion(
                        eq(PROJECT_ID),
                        eq(userDetails),
                        eq(request)
                )
        ).willReturn(response);

        // when & then
        mockMvc.perform(
                        post("/api/v1/projects/{projectId}/questions",
                                PROJECT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("프로젝트 질문 작성에 성공했습니다."))
                .andExpect(jsonPath("$.payload.questionId")
                        .value(QUESTION_ID))
                .andExpect(jsonPath("$.payload.content")
                        .value("질문 내용입니다."))
                .andExpect(jsonPath("$.payload.secret")
                        .value(true))
                .andExpect(jsonPath("$.payload.answerContent")
                        .doesNotExist());

        verify(projectQuestionService).createQuestion(
                PROJECT_ID,
                userDetails,
                request
        );
    }

    @Test
    @DisplayName("질문 내용이 비어 있으면 질문 작성에 실패한다")
    void createQuestionWithBlankContent() throws Exception {
        // given
        ProjectQuestionCreateRequest request =
                new ProjectQuestionCreateRequest(
                        " ",
                        false
                );

        // when & then
        mockMvc.perform(
                        post("/api/v1/projects/{projectId}/questions",
                                PROJECT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("프로젝트 질문에 답변을 저장한다")
    void answerQuestion() throws Exception {
        // given
        ProjectQuestionAnswerRequest request =
                new ProjectQuestionAnswerRequest(
                        "답변 내용입니다."
                );

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 12, 14, 30);
        LocalDateTime answeredAt = createdAt.plusHours(1);

        ProjectQuestionDetailResponse response =
                new ProjectQuestionDetailResponse(
                        QUESTION_ID,
                        PROJECT_ID,
                        USER_ID,
                        UserRole.CREW,
                        "김크루",
                        "질문 내용입니다.",
                        false,
                        "답변 내용입니다.",
                        answeredAt,
                        createdAt,
                        answeredAt
                );

        given(
                projectQuestionService.answerQuestion(
                        eq(PROJECT_ID),
                        eq(QUESTION_ID),
                        eq(userDetails),
                        eq(request)
                )
        ).willReturn(response);

        // when & then
        mockMvc.perform(
                        patch(
                                "/api/v1/projects/{projectId}/questions/{questionId}/answer",
                                PROJECT_ID,
                                QUESTION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("프로젝트 질문 답변 저장에 성공했습니다."))
                .andExpect(jsonPath("$.payload.questionId")
                        .value(QUESTION_ID))
                .andExpect(jsonPath("$.payload.answerContent")
                        .value("답변 내용입니다."));

        verify(projectQuestionService).answerQuestion(
                PROJECT_ID,
                QUESTION_ID,
                userDetails,
                request
        );
    }

    @Test
    @DisplayName("답변 내용이 비어 있으면 답변 저장에 실패한다")
    void answerQuestionWithBlankContent() throws Exception {
        // given
        ProjectQuestionAnswerRequest request =
                new ProjectQuestionAnswerRequest(" ");

        // when & then
        mockMvc.perform(
                        patch(
                                "/api/v1/projects/{projectId}/questions/{questionId}/answer",
                                PROJECT_ID,
                                QUESTION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}