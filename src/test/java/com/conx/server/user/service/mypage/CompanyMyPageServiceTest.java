package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.repository.CrewBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.company.request.CompanyEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationConfirmRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationSendRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyEmailVerificationConfirmResponse;
import com.conx.server.user.service.common.CompanyEmailVerificationService;
import com.conx.server.user.service.common.UserFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CompanyMyPageServiceTest {

    private static final Long COMPANY_ID = 1L;

    private static final String CURRENT_EMAIL =
            "company@test.com";

    private static final String NEW_EMAIL =
            "new-company@test.com";

    private static final String CURRENT_PASSWORD =
            "current-password";

    private static final String ENCODED_PASSWORD =
            "encoded-current-password";

    private static final String VERIFICATION_TOKEN =
            "verification-token";

    @Mock
    private UserFinder userFinder;

    @Mock
    private CrewBookmarkRepository crewBookmarkRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CompanyEmailVerificationService
            companyEmailVerificationService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private Company company;

    @InjectMocks
    private CompanyMyPageService companyMyPageService;

    @Test
    @DisplayName("현재 비밀번호를 확인하고 새 이메일로 인증번호를 발송한다")
    void sendEmailChangeVerification() {
        CompanyEmailVerificationSendRequest request =
                new CompanyEmailVerificationSendRequest(
                        CURRENT_PASSWORD,
                        NEW_EMAIL
                );

        given(userFinder.findActiveCompany(COMPANY_ID))
                .willReturn(company);

        given(company.getPassword())
                .willReturn(ENCODED_PASSWORD);

        given(company.getEmail())
                .willReturn(CURRENT_EMAIL);

        given(company.getId())
                .willReturn(COMPANY_ID);

        given(
                passwordEncoder.matches(
                        CURRENT_PASSWORD,
                        ENCODED_PASSWORD
                )
        ).willReturn(true);

        given(userFinder.existUserByEmail(NEW_EMAIL))
                .willReturn(false);

        companyMyPageService.sendEmailChangeVerification(
                COMPANY_ID,
                request
        );

        verify(companyEmailVerificationService)
                .sendVerificationCode(
                        COMPANY_ID,
                        NEW_EMAIL
                );
    }

    @Test
    @DisplayName("현재 비밀번호가 다르면 이메일 변경 인증번호를 발송하지 않는다")
    void sendEmailChangeVerificationWithWrongPassword() {
        CompanyEmailVerificationSendRequest request =
                new CompanyEmailVerificationSendRequest(
                        "wrong-password",
                        NEW_EMAIL
                );

        given(userFinder.findActiveCompany(COMPANY_ID))
                .willReturn(company);

        given(company.getPassword())
                .willReturn(ENCODED_PASSWORD);

        given(
                passwordEncoder.matches(
                        "wrong-password",
                        ENCODED_PASSWORD
                )
        ).willReturn(false);

        assertThrows(
                CustomException.class,
                () -> companyMyPageService
                        .sendEmailChangeVerification(
                                COMPANY_ID,
                                request
                        )
        );

        verifyNoInteractions(
                companyEmailVerificationService
        );

        verify(
                userFinder,
                never()
        ).existUserByEmail(NEW_EMAIL);
    }

    @Test
    @DisplayName("이미 사용 중인 이메일로는 인증번호를 발송하지 않는다")
    void sendEmailChangeVerificationWithDuplicatedEmail() {
        CompanyEmailVerificationSendRequest request =
                new CompanyEmailVerificationSendRequest(
                        CURRENT_PASSWORD,
                        NEW_EMAIL
                );

        given(userFinder.findActiveCompany(COMPANY_ID))
                .willReturn(company);

        given(company.getPassword())
                .willReturn(ENCODED_PASSWORD);

        given(company.getEmail())
                .willReturn(CURRENT_EMAIL);

        given(
                passwordEncoder.matches(
                        CURRENT_PASSWORD,
                        ENCODED_PASSWORD
                )
        ).willReturn(true);

        given(userFinder.existUserByEmail(NEW_EMAIL))
                .willReturn(true);

        assertThrows(
                CustomException.class,
                () -> companyMyPageService
                        .sendEmailChangeVerification(
                                COMPANY_ID,
                                request
                        )
        );

        verifyNoInteractions(
                companyEmailVerificationService
        );
    }

    @Test
    @DisplayName("새 이메일 인증번호 확인 후 변경용 토큰을 반환한다")
    void confirmEmailChangeVerification() {
        CompanyEmailVerificationConfirmRequest request =
                new CompanyEmailVerificationConfirmRequest(
                        NEW_EMAIL,
                        123456
                );

        given(userFinder.findActiveCompany(COMPANY_ID))
                .willReturn(company);

        given(company.getEmail())
                .willReturn(CURRENT_EMAIL);

        given(company.getId())
                .willReturn(COMPANY_ID);

        given(userFinder.existUserByEmail(NEW_EMAIL))
                .willReturn(false);

        given(
                companyEmailVerificationService
                        .confirmVerificationCode(
                                COMPANY_ID,
                                NEW_EMAIL,
                                123456
                        )
        ).willReturn(VERIFICATION_TOKEN);

        CompanyEmailVerificationConfirmResponse response =
                companyMyPageService
                        .confirmEmailChangeVerification(
                                COMPANY_ID,
                                request
                        );

        assertEquals(
                VERIFICATION_TOKEN,
                response.verificationToken()
        );
    }

    @Test
    @DisplayName("인증된 새 이메일로 계정 이메일을 변경한다")
    void updateEmail() {
        CompanyEmailUpdateRequest request =
                new CompanyEmailUpdateRequest(
                        CURRENT_PASSWORD,
                        NEW_EMAIL,
                        VERIFICATION_TOKEN
                );

        given(userFinder.findActiveCompany(COMPANY_ID))
                .willReturn(company);

        given(company.getPassword())
                .willReturn(ENCODED_PASSWORD);

        given(company.getEmail())
                .willReturn(CURRENT_EMAIL);

        given(company.getId())
                .willReturn(COMPANY_ID);

        given(company.getRole())
                .willReturn(UserRole.COMPANY);

        given(
                passwordEncoder.matches(
                        CURRENT_PASSWORD,
                        ENCODED_PASSWORD
                )
        ).willReturn(true);

        given(userFinder.existUserByEmail(NEW_EMAIL))
                .willReturn(false);

        CompanyAccountResponse response =
                companyMyPageService.updateEmail(
                        COMPANY_ID,
                        request
                );

        verify(companyEmailVerificationService)
                .consumeVerificationToken(
                        COMPANY_ID,
                        NEW_EMAIL,
                        VERIFICATION_TOKEN
                );

        verify(company)
                .changeEmail(NEW_EMAIL);

        verify(redisTemplate)
                .delete(
                        "refreshToken:"
                                + UserRole.COMPANY.getRole()
                                + ":"
                                + COMPANY_ID
                );

        // Mock Company의 getter 값은 자동으로 변경되지 않으므로
        // 반환 객체 존재 여부만 확인하고 실제 변경 호출은 verify로 검증한다.
        org.junit.jupiter.api.Assertions.assertNotNull(
                response
        );
    }

    @Test
    @DisplayName("이메일 변경 시 인증 토큰 검증이 실패하면 이메일을 변경하지 않는다")
    void updateEmailWithInvalidVerificationToken() {
        CompanyEmailUpdateRequest request =
                new CompanyEmailUpdateRequest(
                        CURRENT_PASSWORD,
                        NEW_EMAIL,
                        "invalid-token"
                );

        given(userFinder.findActiveCompany(COMPANY_ID))
                .willReturn(company);

        given(company.getPassword())
                .willReturn(ENCODED_PASSWORD);

        given(company.getEmail())
                .willReturn(CURRENT_EMAIL);

        given(company.getId())
                .willReturn(COMPANY_ID);

        given(
                passwordEncoder.matches(
                        CURRENT_PASSWORD,
                        ENCODED_PASSWORD
                )
        ).willReturn(true);

        given(userFinder.existUserByEmail(NEW_EMAIL))
                .willReturn(false);

        org.mockito.Mockito.doThrow(
                        CustomException.class
                ).when(companyEmailVerificationService)
                .consumeVerificationToken(
                        COMPANY_ID,
                        NEW_EMAIL,
                        "invalid-token"
                );

        assertThrows(
                CustomException.class,
                () -> companyMyPageService.updateEmail(
                        COMPANY_ID,
                        request
                )
        );

        verify(
                company,
                never()
        ).changeEmail(NEW_EMAIL);

        verify(
                redisTemplate,
                never()
        ).delete(
                "refreshToken:"
                        + UserRole.COMPANY.getRole()
                        + ":"
                        + COMPANY_ID
        );
    }
}