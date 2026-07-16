package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.user.dto.company.request.CompanyAccountUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyProfileUpdateRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import com.conx.server.user.dto.company.response.CompanyCrewBookmarkToggleResponse;
import com.conx.server.user.dto.company.response.CompanyProfileResponse;
import com.conx.server.user.service.mypage.CompanyMyPageService;
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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyMyPageControllerTest {

    private static final Long COMPANY_ID = 1L;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CompanyMyPageService companyMyPageService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory = new ApiResponseFactory(notificationRepository);

        CompanyMyPageController companyMyPageController = new CompanyMyPageController(
                companyMyPageService,
                apiResponseFactory
        );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        given(userDetails.getId()).willReturn(COMPANY_ID);
        given(notificationRepository.existsByreceiverIdAndIsRead(COMPANY_ID, false))
                .willReturn(false);

        mockMvc = MockMvcBuilders
                .standaloneSetup(companyMyPageController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
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
                })
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("기업 프로필을 조회한다")
    void getProfile() throws Exception {
        // given
        CompanyProfileResponse response = new CompanyProfileResponse(
                "테스트 기업",
                "기업 소개입니다.",
                "테스트 브랜드",
                null,
                null,
                "profile-image.png",
                "additional-file-link",
                "https://company.com"
        );

        given(companyMyPageService.getProfile(COMPANY_ID)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 프로필 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.companyName").value("테스트 기업"))
                .andExpect(jsonPath("$.payload.companyIntroduction").value("기업 소개입니다."))
                .andExpect(jsonPath("$.payload.brandName").value("테스트 브랜드"))
                .andExpect(jsonPath("$.payload.profileImage").value("profile-image.png"))
                .andExpect(jsonPath("$.payload.homepageLink").value("https://company.com"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyMyPageService).getProfile(COMPANY_ID);
    }

    @Test
    @DisplayName("기업 프로필을 수정한다")
    void updateProfile() throws Exception {
        // given
        CompanyProfileUpdateRequest request = new CompanyProfileUpdateRequest(
                "수정 기업",
                "수정된 기업 소개입니다.",
                "수정 브랜드",
                null,
                null,
                "new-profile-image.png",
                "new-additional-file-link",
                "https://new-company.com"
        );

        CompanyProfileResponse response = new CompanyProfileResponse(
                "수정 기업",
                "수정된 기업 소개입니다.",
                "수정 브랜드",
                null,
                null,
                "new-profile-image.png",
                "new-additional-file-link",
                "https://new-company.com"
        );

        given(companyMyPageService.updateProfile(eq(COMPANY_ID), eq(request)))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/v1/companies/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 프로필 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.companyName").value("수정 기업"))
                .andExpect(jsonPath("$.payload.companyIntroduction").value("수정된 기업 소개입니다."))
                .andExpect(jsonPath("$.payload.brandName").value("수정 브랜드"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyMyPageService).updateProfile(eq(COMPANY_ID), eq(request));
    }

    @Test
    @DisplayName("기업 계정 정보를 조회한다")
    void getAccount() throws Exception {
        // given
        CompanyAccountResponse response = new CompanyAccountResponse(
                "company@test.com",
                "테스트 기업",
                "123-45-67890",
                "담당자",
                "마케팅 매니저"
        );

        given(companyMyPageService.getAccount(COMPANY_ID)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 계정 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.email").value("company@test.com"))
                .andExpect(jsonPath("$.payload.companyName").value("테스트 기업"))
                .andExpect(jsonPath("$.payload.businessRegistrationNumber").value("123-45-67890"))
                .andExpect(jsonPath("$.payload.managerName").value("담당자"))
                .andExpect(jsonPath("$.payload.job").value("마케팅 매니저"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyMyPageService).getAccount(COMPANY_ID);
    }

    @Test
    @DisplayName("기업 계정 정보를 수정한다")
    void updateAccount() throws Exception {
        // given
        CompanyAccountUpdateRequest request = new CompanyAccountUpdateRequest(
                "수정 기업",
                "987-65-43210",
                "수정 담당자",
                "브랜드 매니저"
        );

        CompanyAccountResponse response = new CompanyAccountResponse(
                "company@test.com",
                "수정 기업",
                "987-65-43210",
                "수정 담당자",
                "브랜드 매니저"
        );

        given(companyMyPageService.updateAccount(eq(COMPANY_ID), eq(request)))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/v1/companies/me/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("기업 계정 정보 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.email").value("company@test.com"))
                .andExpect(jsonPath("$.payload.companyName").value("수정 기업"))
                .andExpect(jsonPath("$.payload.businessRegistrationNumber").value("987-65-43210"))
                .andExpect(jsonPath("$.payload.managerName").value("수정 담당자"))
                .andExpect(jsonPath("$.payload.job").value("브랜드 매니저"))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyMyPageService).updateAccount(eq(COMPANY_ID), eq(request));
    }

    @Test
    @DisplayName("크루 북마크 상태를 변경한다")
    void toggleCrewBookmark() throws Exception {
        // given
        Long crewId = 10L;

        CompanyCrewBookmarkToggleResponse response =
                new CompanyCrewBookmarkToggleResponse(crewId, true);

        given(companyMyPageService.toggleCrewBookmark(COMPANY_ID, crewId))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/v1/companies/me/bookmarked-crews/{crewId}", crewId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("크루 북마크 상태가 변경되었습니다."))
                .andExpect(jsonPath("$.payload.crewId").value(10))
                .andExpect(jsonPath("$.payload.bookmarked").value(true))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyMyPageService).toggleCrewBookmark(COMPANY_ID, crewId);
    }

    @Test
    @DisplayName("북마크한 크루 목록을 조회한다")
    void getBookmarkedCrews() throws Exception {
        // given
        CompanyBookmarkedCrewResponse crew = new CompanyBookmarkedCrewResponse(
                10L,
                "crew-profile-image.png",
                "테스트 크루",
                "크루 소개입니다.",
                null,
                null,
                null,
                5,
                300000
        );

        given(companyMyPageService.getBookmarkedCrews(COMPANY_ID))
                .willReturn(List.of(crew));

        // when & then
        mockMvc.perform(get("/api/v1/companies/me/bookmarked-crews"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("북마크한 크루 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].crewId").value(10))
                .andExpect(jsonPath("$.payload[0].crewName").value("테스트 크루"))
                .andExpect(jsonPath("$.payload[0].crewIntroduction").value("크루 소개입니다."))
                .andExpect(jsonPath("$.payload[0].memberAmount").value(5))
                .andExpect(jsonPath("$.payload[0].totalProject").value(300000))
                .andExpect(jsonPath("$.hasNotification").value(false));

        verify(companyMyPageService).getBookmarkedCrews(COMPANY_ID);
    }
}