package com.conx.server.user.controller.crew;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.dto.crew.response.CrewFileResponse;
import com.conx.server.user.dto.crew.response.CrewLinkResponse;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.service.browse.CrewBrowseService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CrewBrowseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrewBrowseService crewBrowseService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory =
                new ApiResponseFactory(notificationRepository);

        CrewBrowseController controller =
                new CrewBrowseController(
                        crewBrowseService,
                        apiResponseFactory
                );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
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
                        new MappingJackson2HttpMessageConverter(
                                objectMapper
                        )
                )
                .build();
    }

    @Test
    @DisplayName("크루 목록을 기본 페이지 조건으로 조회한다")
    void getCrews() throws Exception {
        CrewBrowseResponse crew =
                new CrewBrowseResponse(
                        1L,
                        "https://example.com/crew.png",
                        "CONX Crew",
                        "마케팅 전문 크루입니다.",
                        Industry.IT,
                        CrewType.ACADEMY,
                        4.5,
                        5,
                        true
                );

        Page<CrewBrowseResponse> response =
                new PageImpl<>(
                        List.of(crew),
                        PageRequest.of(0, 12),
                        1
                );

        given(
                crewBrowseService.getCrews(
                        null,
                        null,
                        null,
                        null,
                        0,
                        12,
                        userDetails
                )
        ).willReturn(response);

        mockMvc.perform(
                        get("/api/v1/crews")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("크루 목록 조회에 성공했습니다.")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].crewId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.payload.content[0].crewName")
                                .value("CONX Crew")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].point")
                                .value(4.5)
                )
                .andExpect(
                        jsonPath("$.payload.content[0].cumulative")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.payload.content[0].bookmarked")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.payload.number")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.payload.size")
                                .value(12)
                );

        verify(crewBrowseService).getCrews(
                null,
                null,
                null,
                null,
                0,
                12,
                userDetails
        );
    }

    @Test
    @DisplayName("크루 상세 조회에서 신규 상세 정보를 반환한다")
    void getCrewDetail() throws Exception {
        CrewBrowseDetailResponse response =
                new CrewBrowseDetailResponse(
                        1L,
                        "https://example.com/crew.png",
                        "CONX Crew",
                        CrewType.ACADEMY,
                        null,

                        List.of(
                                "서강대학교",
                                "연세대학교"
                        ),
                        10,
                        Industry.IT,

                        "마케팅 전문 크루입니다.\n\n추가 소개입니다.",
                        List.of(
                                "기획",
                                "콘텐츠 제작"
                        ),
                        List.of(
                                "SNS 운영",
                                "숏폼 제작"
                        ),

                        List.of(
                                new CrewLinkResponse(
                                        1L,
                                        "인스타그램",
                                        "https://instagram.com/conx",
                                        "크루 공식 인스타그램"
                                )
                        ),
                        List.of(
                                new CrewFileResponse(
                                        1L,
                                        "크루소개서.pdf",
                                        "pdf",
                                        1048576L,
                                        "https://example.com/crew.pdf",
                                        "크루 소개 자료"
                                )
                        ),
                        List.of(),
                        List.of(
                                new CrewProjectHistoryResponse(
                                        100L,
                                        ProjectStatus.DONE,
                                        "SNS 콘텐츠 제작",
                                        "CONX",
                                        null,
                                        "인스타그램",
                                        "릴스 영상",
                                        4.6,
                                        null,
                                        null
                                )
                        ),

                        true,
                        true,
                        4.6,
                        1000000
                );

        given(
                crewBrowseService.getCrewDetail(
                        1L,
                        userDetails
                )
        ).willReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/crews/{crewId}",
                                1L
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("크루 상세 조회에 성공했습니다.")
                )
                .andExpect(
                        jsonPath("$.payload.crewId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.payload.schools[0]")
                                .value("서강대학교")
                )
                .andExpect(
                        jsonPath("$.payload.schools[1]")
                                .value("연세대학교")
                )
                .andExpect(
                        jsonPath("$.payload.crewIntroduction")
                                .value(
                                        "마케팅 전문 크루입니다.\n\n추가 소개입니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.specialties[0]")
                                .value("SNS 운영")
                )
                .andExpect(
                        jsonPath("$.payload.links[0].name")
                                .value("인스타그램")
                )
                .andExpect(
                        jsonPath("$.payload.files[0].fileName")
                                .value("크루소개서.pdf")
                )
                .andExpect(
                        jsonPath("$.payload.files[0].extension")
                                .value("pdf")
                )
                .andExpect(
                        jsonPath("$.payload.files[0].size")
                                .value(1048576)
                )
                .andExpect(
                        jsonPath(
                                "$.payload.representativeProjects[0].projectName"
                        ).value("SNS 콘텐츠 제작")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.representativeProjects[0].platformName"
                        ).value("인스타그램")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.representativeProjects[0].contentType"
                        ).value("릴스 영상")
                )
                .andExpect(
                        jsonPath(
                                "$.payload.representativeProjects[0].point"
                        ).value(4.6)
                )
                .andExpect(
                        jsonPath("$.payload.hasPublicDetail")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.payload.bookmarked")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.payload.point")
                                .value(4.6)
                );

        verify(crewBrowseService).getCrewDetail(
                1L,
                userDetails
        );
    }

    @Test
    @DisplayName("크루 대표 프로젝트 전체보기를 조회한다")
    void getCrewProjects() throws Exception {
        CrewProjectHistoryResponse project =
                new CrewProjectHistoryResponse(
                        100L,
                        ProjectStatus.DONE,
                        "SNS 콘텐츠 제작",
                        "CONX",
                        null,
                        "인스타그램",
                        "릴스 영상",
                        4.6,
                        null,
                        null
                );

        Page<CrewProjectHistoryResponse> response =
                new PageImpl<>(
                        List.of(project),
                        PageRequest.of(0, 8),
                        1
                );

        given(
                crewBrowseService.getCrewProjects(
                        1L,
                        0,
                        8,
                        CrewProjectHistorySort.RECENT
                )
        ).willReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/crews/{crewId}/projects",
                                1L
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("크루 프로젝트 이력 조회에 성공했습니다.")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].projectId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.payload.content[0].status")
                                .value("DONE")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].projectName")
                                .value("SNS 콘텐츠 제작")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].platformName")
                                .value("인스타그램")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].contentType")
                                .value("릴스 영상")
                )
                .andExpect(
                        jsonPath("$.payload.content[0].point")
                                .value(4.6)
                )
                .andExpect(
                        jsonPath("$.payload.number")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.payload.size")
                                .value(8)
                );

        verify(crewBrowseService).getCrewProjects(
                1L,
                0,
                8,
                CrewProjectHistorySort.RECENT
        );
    }
}