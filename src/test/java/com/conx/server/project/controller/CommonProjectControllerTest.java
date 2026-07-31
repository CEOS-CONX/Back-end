package com.conx.server.project.controller;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.service.CommonProjectService;
import com.conx.server.user.dto.company.response.ProjectInspectionWrapperDTO;
import com.conx.server.user.dto.company.response.ProjectStatusResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommonProjectControllerTest {

    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "kdhyun422@gmail.com";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CommonProjectService commonProjectService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory =
                new ApiResponseFactory(notificationRepository);

        CommonProjectController controller =
                new CommonProjectController(
                        apiResponseFactory,
                        commonProjectService
                );

        objectMapper = new ObjectMapper();

        lenient()
                .when(userDetails.getId())
                .thenReturn(USER_ID);

        lenient()
                .when(userDetails.getUserEmail())
                .thenReturn(USER_EMAIL);

        lenient()
                .when(notificationRepository.existsByreceiverIdAndIsRead(USER_ID, false))
                .thenReturn(false);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(MethodParameter parameter) {
                                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
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
    @DisplayName("프로젝트 결과물 상세를 조회한다")
    void getProjectReviewDetail() throws Exception {
        Long projectId = 1L;
        Long submissionId = 2L;

        ProjectInspectionWrapperDTO response =
                org.mockito.Mockito.mock(ProjectInspectionWrapperDTO.class);

        given(
                commonProjectService.getProjectReviewDetail(
                        USER_EMAIL,
                        projectId,
                        submissionId
                )
        ).willReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/projects/{projectId}/submissions/{submissionId}",
                                projectId,
                                submissionId
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value("상세 결과물 공유내역 조회에 성공했습니다.")
                );

        org.mockito.Mockito.verify(commonProjectService)
                .getProjectReviewDetail(
                        USER_EMAIL,
                        projectId,
                        submissionId
                );
    }
}