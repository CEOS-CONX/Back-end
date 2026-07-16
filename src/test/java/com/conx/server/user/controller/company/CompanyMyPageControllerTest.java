package com.conx.server.user.controller.company;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.user.dto.company.request.CompanyJobUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyNameUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyPasswordUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyProfileUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyRepresentativeEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyRepresentativePhoneUpdateRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import com.conx.server.user.dto.company.response.CompanyCrewBookmarkToggleResponse;
import com.conx.server.user.dto.company.response.CompanyProfileResponse;
import com.conx.server.user.service.mypage.CompanyMyPageService;
import com.conx.server.user.dto.company.request.CompanyEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationConfirmRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationSendRequest;
import com.conx.server.user.dto.company.response.CompanyEmailVerificationConfirmResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        ApiResponseFactory apiResponseFactory =
                new ApiResponseFactory(notificationRepository);

        CompanyMyPageController companyMyPageController =
                new CompanyMyPageController(
                        companyMyPageService,
                        apiResponseFactory
                );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        given(userDetails.getId()).willReturn(COMPANY_ID);
        given(
                notificationRepository.existsByreceiverIdAndIsRead(
                        COMPANY_ID,
                        false
                )
        ).willReturn(false);

        mockMvc = MockMvcBuilders
                .standaloneSetup(companyMyPageController)
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
    @DisplayName("기업 프로필을 조회한다")
    void getProfile() throws Exception {
        CompanyProfileResponse response =
                new CompanyProfileResponse(
                        "테스트 기업",
                        "기업 소개입니다.",
                        "테스트 브랜드",
                        null,
                        "123-45-67890",
                        "profile-image.png",
                        "additional-file-link",
                        "https://company.com"
                );

        given(companyMyPageService.getProfile(COMPANY_ID))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("기업 프로필 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.companyName")
                        .value("테스트 기업"))
                .andExpect(jsonPath("$.payload.companyIntroduction")
                        .value("기업 소개입니다."))
                .andExpect(jsonPath("$.payload.brandName")
                        .value("테스트 브랜드"))
                .andExpect(jsonPath("$.payload.businessRegistrationNumber")
                        .value("123-45-67890"))
                .andExpect(jsonPath("$.payload.profileImage")
                        .value("profile-image.png"))
                .andExpect(jsonPath("$.payload.website")
                        .value("https://company.com"))
                .andExpect(jsonPath("$.payload.customIndustry")
                        .doesNotExist())
                .andExpect(jsonPath("$.payload.homepageLink")
                        .doesNotExist())
                .andExpect(jsonPath("$.hasNotification")
                        .value(false));

        verify(companyMyPageService).getProfile(COMPANY_ID);
    }

    @Test
    @DisplayName("기업 프로필을 수정한다")
    void updateProfile() throws Exception {
        CompanyProfileUpdateRequest request =
                new CompanyProfileUpdateRequest(
                        "수정 기업",
                        "수정된 기업 소개입니다.",
                        "수정 브랜드",
                        null,
                        null,
                        "new-profile-image.png",
                        "new-additional-file-link",
                        "https://new-company.com",
                        "987-65-43210"
                );

        CompanyProfileResponse response =
                new CompanyProfileResponse(
                        "수정 기업",
                        "수정된 기업 소개입니다.",
                        "수정 브랜드",
                        null,
                        "987-65-43210",
                        "new-profile-image.png",
                        "new-additional-file-link",
                        "https://new-company.com"
                );

        given(
                companyMyPageService.updateProfile(
                        eq(COMPANY_ID),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        patch("/api/v1/companies/me/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("기업 프로필 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.companyName")
                        .value("수정 기업"))
                .andExpect(jsonPath("$.payload.businessRegistrationNumber")
                        .value("987-65-43210"))
                .andExpect(jsonPath("$.payload.website")
                        .value("https://new-company.com"))
                .andExpect(jsonPath("$.hasNotification")
                        .value(false));

        verify(companyMyPageService).updateProfile(
                eq(COMPANY_ID),
                eq(request)
        );
    }

    @Test
    @DisplayName("기업 계정 정보를 조회한다")
    void getAccount() throws Exception {
        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "담당자",
                        "company@test.com",
                        "마케팅 매니저",
                        "02-1234-5678",
                        "contact@company.com"
                );

        given(companyMyPageService.getAccount(COMPANY_ID))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/companies/me/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("기업 계정 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.name")
                        .value("담당자"))
                .andExpect(jsonPath("$.payload.email")
                        .value("company@test.com"))
                .andExpect(jsonPath("$.payload.job")
                        .value("마케팅 매니저"))
                .andExpect(jsonPath("$.payload.representativePhone")
                        .value("02-1234-5678"))
                .andExpect(jsonPath("$.payload.representativeEmail")
                        .value("contact@company.com"))
                .andExpect(jsonPath("$.payload.companyName")
                        .doesNotExist())
                .andExpect(jsonPath("$.payload.businessRegistrationNumber")
                        .doesNotExist())
                .andExpect(jsonPath("$.hasNotification")
                        .value(false));

        verify(companyMyPageService).getAccount(COMPANY_ID);
    }

    @Test
    @DisplayName("기업 계정 이름을 수정한다")
    void updateName() throws Exception {
        CompanyNameUpdateRequest request =
                new CompanyNameUpdateRequest(
                        "current-password",
                        "새 담당자"
                );

        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "새 담당자",
                        "company@test.com",
                        "마케팅 매니저",
                        "02-1234-5678",
                        "contact@company.com"
                );

        given(companyMyPageService.updateName(
                eq(COMPANY_ID),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(
                        patch("/api/v1/companies/me/account/name")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("이름 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.name")
                        .value("새 담당자"));

        verify(companyMyPageService).updateName(
                eq(COMPANY_ID),
                eq(request)
        );
    }

    @Test
    @DisplayName("기업 담당자 직무를 수정한다")
    void updateJob() throws Exception {
        CompanyJobUpdateRequest request =
                new CompanyJobUpdateRequest(
                        "current-password",
                        "브랜드 매니저"
                );

        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "담당자",
                        "company@test.com",
                        "브랜드 매니저",
                        "02-1234-5678",
                        "contact@company.com"
                );

        given(companyMyPageService.updateJob(
                eq(COMPANY_ID),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(
                        patch("/api/v1/companies/me/account/job")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("직무 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.job")
                        .value("브랜드 매니저"));

        verify(companyMyPageService).updateJob(
                eq(COMPANY_ID),
                eq(request)
        );
    }

    @Test
    @DisplayName("기업 대표 전화번호를 수정한다")
    void updateRepresentativePhone() throws Exception {
        CompanyRepresentativePhoneUpdateRequest request =
                new CompanyRepresentativePhoneUpdateRequest(
                        "current-password",
                        "02-9876-5432"
                );

        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "담당자",
                        "company@test.com",
                        "마케팅 매니저",
                        "02-9876-5432",
                        "contact@company.com"
                );

        given(companyMyPageService.updateRepresentativePhone(
                eq(COMPANY_ID),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/account/representative-phone"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("대표 전화번호 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.representativePhone")
                        .value("02-9876-5432"));

        verify(companyMyPageService)
                .updateRepresentativePhone(
                        eq(COMPANY_ID),
                        eq(request)
                );
    }

    @Test
    @DisplayName("기업 대표 이메일을 수정한다")
    void updateRepresentativeEmail() throws Exception {
        CompanyRepresentativeEmailUpdateRequest request =
                new CompanyRepresentativeEmailUpdateRequest(
                        "current-password",
                        "new-contact@company.com"
                );

        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "담당자",
                        "company@test.com",
                        "마케팅 매니저",
                        "02-1234-5678",
                        "new-contact@company.com"
                );

        given(companyMyPageService.updateRepresentativeEmail(
                eq(COMPANY_ID),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/account/representative-email"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("대표 이메일 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.representativeEmail")
                        .value("new-contact@company.com"));

        verify(companyMyPageService)
                .updateRepresentativeEmail(
                        eq(COMPANY_ID),
                        eq(request)
                );
    }

    @Test
    @DisplayName("기업 계정 비밀번호를 수정한다")
    void updatePassword() throws Exception {
        CompanyPasswordUpdateRequest request =
                new CompanyPasswordUpdateRequest(
                        "current-password",
                        "new-password",
                        "new-password"
                );

        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "담당자",
                        "company@test.com",
                        "마케팅 매니저",
                        "02-1234-5678",
                        "contact@company.com"
                );

        given(companyMyPageService.updatePassword(
                eq(COMPANY_ID),
                eq(request)
        )).willReturn(response);

        mockMvc.perform(
                        patch("/api/v1/companies/me/account/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("비밀번호 수정에 성공했습니다."))
                .andExpect(jsonPath("$.payload.email")
                        .value("company@test.com"));

        verify(companyMyPageService).updatePassword(
                eq(COMPANY_ID),
                eq(request)
        );
    }

    @Test
    @DisplayName("크루 북마크 상태를 변경한다")
    void toggleCrewBookmark() throws Exception {
        Long crewId = 10L;

        CompanyCrewBookmarkToggleResponse response =
                new CompanyCrewBookmarkToggleResponse(
                        crewId,
                        true
                );

        given(
                companyMyPageService.toggleCrewBookmark(
                        COMPANY_ID,
                        crewId
                )
        ).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/bookmarked-crews/{crewId}",
                                crewId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("크루 북마크 상태가 변경되었습니다."))
                .andExpect(jsonPath("$.payload.crewId")
                        .value(10))
                .andExpect(jsonPath("$.payload.bookmarked")
                        .value(true));

        verify(companyMyPageService)
                .toggleCrewBookmark(COMPANY_ID, crewId);
    }

    @Test
    @DisplayName("북마크한 크루 목록을 조회한다")
    void getBookmarkedCrews() throws Exception {
        CompanyBookmarkedCrewResponse crew =
                new CompanyBookmarkedCrewResponse(
                        10L,
                        "crew-profile-image.png",
                        "테스트 크루",
                        "크루 소개입니다.",
                        null,
                        null,
                        null,
                        5,
                        300000,
                        4.5
                );

        given(companyMyPageService.getBookmarkedCrews(COMPANY_ID))
                .willReturn(List.of(crew));

        mockMvc.perform(
                        get("/api/v1/companies/me/bookmarked-crews")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("북마크한 크루 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].crewId")
                        .value(10))
                .andExpect(jsonPath("$.payload[0].crewName")
                        .value("테스트 크루"))
                .andExpect(jsonPath("$.payload[0].memberAmount")
                        .value(5))
                .andExpect(jsonPath("$.payload[0].cumulative")
                        .value(300000))
                .andExpect(jsonPath("$.payload[0].point")
                        .value(4.5));

        verify(companyMyPageService)
                .getBookmarkedCrews(COMPANY_ID);
    }

    @Test
    @DisplayName("계정 이메일 변경 인증번호를 발송한다")
    void sendEmailChangeVerification() throws Exception {
        CompanyEmailVerificationSendRequest request =
                new CompanyEmailVerificationSendRequest(
                        "current-password",
                        "new-company@test.com"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/account/email/verifications"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("success"))
                .andExpect(jsonPath("$.message")
                        .value("새 이메일로 인증번호를 발송했습니다."))
                .andExpect(jsonPath("$.hasNotification")
                        .value(false));

        verify(companyMyPageService)
                .sendEmailChangeVerification(
                        eq(COMPANY_ID),
                        eq(request)
                );
    }

    @Test
    @DisplayName("계정 이메일 변경 인증번호를 확인한다")
    void confirmEmailChangeVerification() throws Exception {
        CompanyEmailVerificationConfirmRequest request =
                new CompanyEmailVerificationConfirmRequest(
                        "new-company@test.com",
                        123456
                );

        CompanyEmailVerificationConfirmResponse response =
                new CompanyEmailVerificationConfirmResponse(
                        "verification-token"
                );

        given(
                companyMyPageService
                        .confirmEmailChangeVerification(
                                eq(COMPANY_ID),
                                eq(request)
                        )
        ).willReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/companies/me/account/email/verifications/confirm"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("success"))
                .andExpect(jsonPath("$.message")
                        .value("새 이메일 인증에 성공했습니다."))
                .andExpect(jsonPath("$.payload.verificationToken")
                        .value("verification-token"))
                .andExpect(jsonPath("$.hasNotification")
                        .value(false));

        verify(companyMyPageService)
                .confirmEmailChangeVerification(
                        eq(COMPANY_ID),
                        eq(request)
                );
    }

    @Test
    @DisplayName("인증된 새 이메일로 계정 이메일을 변경한다")
    void updateEmail() throws Exception {
        CompanyEmailUpdateRequest request =
                new CompanyEmailUpdateRequest(
                        "current-password",
                        "new-company@test.com",
                        "verification-token"
                );

        CompanyAccountResponse response =
                new CompanyAccountResponse(
                        "담당자",
                        "new-company@test.com",
                        "마케팅 매니저",
                        "02-1234-5678",
                        "contact@company.com"
                );

        given(
                companyMyPageService.updateEmail(
                        eq(COMPANY_ID),
                        eq(request)
                )
        ).willReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/v1/companies/me/account/email"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("success"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "계정 이메일 변경에 성공했습니다. 다시 로그인해주세요."
                        ))
                .andExpect(jsonPath("$.payload.email")
                        .value("new-company@test.com"))
                .andExpect(jsonPath("$.hasNotification")
                        .value(false));

        verify(companyMyPageService).updateEmail(
                eq(COMPANY_ID),
                eq(request)
        );
    }
}