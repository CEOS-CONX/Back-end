package com.conx.server.user.service.common;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.mailSender.EmailDTO;
import com.conx.server.global.mailSender.MailSender;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.passwordReset.request.PasswordResetRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationConfirmRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationSendRequest;
import com.conx.server.user.dto.passwordReset.response.PasswordResetVerificationConfirmResponse;
import com.conx.server.user.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    private static final Long COMPANY_ID = 1L;

    private static final String NAME =
            "담당자";

    private static final String EMAIL =
            "company@test.com";

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private MailSender mailSender;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Company company;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    @DisplayName("이름과 이메일이 일치하는 기업에 비밀번호 재설정 인증번호를 발송한다")
    void sendVerificationCode() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        PasswordResetVerificationSendRequest request =
                new PasswordResetVerificationSendRequest(
                        NAME,
                        EMAIL
                );

        given(
                companyRepository
                        .findByEmailIgnoreCaseAndManagerNameAndStatus(
                                EMAIL,
                                NAME,
                                UserStatus.ACTIVE
                        )
        ).willReturn(Optional.of(company));

        given(company.getId())
                .willReturn(COMPANY_ID);

        given(company.getEmail())
                .willReturn(EMAIL);

        passwordResetService.sendVerificationCode(
                request
        );

        verify(valueOperations).set(
                eq(
                        "verification:reset-password:company:"
                                + EMAIL
                ),
                matches(
                        COMPANY_ID
                                + "\\|\\d{6}"
                ),
                eq(Duration.ofMinutes(5))
        );

        ArgumentCaptor<EmailDTO> emailCaptor =
                ArgumentCaptor.forClass(
                        EmailDTO.class
                );

        verify(mailSender)
                .sendMail(emailCaptor.capture());

        org.junit.jupiter.api.Assertions.assertEquals(
                EMAIL,
                emailCaptor.getValue().getReceiver()
        );
    }

    @Test
    @DisplayName("일치하는 계정이 없어도 동일한 성공 흐름으로 종료하고 메일은 보내지 않는다")
    void sendVerificationCodeWhenCompanyDoesNotExist() {
        PasswordResetVerificationSendRequest request =
                new PasswordResetVerificationSendRequest(
                        NAME,
                        EMAIL
                );

        given(
                companyRepository
                        .findByEmailIgnoreCaseAndManagerNameAndStatus(
                                EMAIL,
                                NAME,
                                UserStatus.ACTIVE
                        )
        ).willReturn(Optional.empty());

        passwordResetService.sendVerificationCode(
                request
        );

        verifyNoInteractions(mailSender);

        verify(
                valueOperations,
                never()
        ).set(
                anyString(),
                anyString(),
                any(Duration.class)
        );
    }

    @Test
    @DisplayName("비밀번호 재설정 인증번호가 일치하면 resetToken을 발급한다")
    void confirmVerificationCode() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        PasswordResetVerificationConfirmRequest request =
                new PasswordResetVerificationConfirmRequest(
                        EMAIL,
                        123456
                );

        String codeKey =
                "verification:reset-password:company:"
                        + EMAIL;

        given(valueOperations.get(codeKey))
                .willReturn(
                        COMPANY_ID + "|123456"
                );

        PasswordResetVerificationConfirmResponse response =
                passwordResetService
                        .confirmVerificationCode(
                                request
                        );

        assertFalse(
                response.resetToken().isBlank()
        );

        verify(valueOperations).set(
                eq(
                        "verified:reset-password:company:"
                                + response.resetToken()
                ),
                eq(
                        COMPANY_ID
                                + "|"
                                + EMAIL
                ),
                eq(Duration.ofMinutes(30))
        );

        verify(redisTemplate)
                .delete(codeKey);
    }

    @Test
    @DisplayName("비밀번호 재설정 인증번호가 다르면 resetToken을 발급하지 않는다")
    void confirmVerificationCodeWithWrongCode() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        PasswordResetVerificationConfirmRequest request =
                new PasswordResetVerificationConfirmRequest(
                        EMAIL,
                        999999
                );

        String codeKey =
                "verification:reset-password:company:"
                        + EMAIL;

        given(valueOperations.get(codeKey))
                .willReturn(
                        COMPANY_ID + "|123456"
                );

        assertThrows(
                CustomException.class,
                () -> passwordResetService
                        .confirmVerificationCode(
                                request
                        )
        );

        verify(
                valueOperations,
                never()
        ).set(
                anyString(),
                anyString(),
                any(Duration.class)
        );
    }

    @Test
    @DisplayName("인증 완료 토큰으로 새 비밀번호를 암호화해 저장한다")
    void resetPassword() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        String resetToken =
                "reset-token";

        String resetTokenKey =
                "verified:reset-password:company:"
                        + resetToken;

        PasswordResetRequest request =
                new PasswordResetRequest(
                        resetToken,
                        "new-password",
                        "new-password"
                );

        given(valueOperations.get(resetTokenKey))
                .willReturn(
                        COMPANY_ID
                                + "|"
                                + EMAIL
                );

        given(
                companyRepository.findByIdAndStatus(
                        COMPANY_ID,
                        UserStatus.ACTIVE
                )
        ).willReturn(Optional.of(company));

        given(company.getEmail())
                .willReturn(EMAIL);

        given(company.getId())
                .willReturn(COMPANY_ID);

        given(company.getRole())
                .willReturn(UserRole.COMPANY);

        given(
                passwordEncoder.encode(
                        "new-password"
                )
        ).willReturn(
                "encoded-new-password"
        );

        passwordResetService.resetPassword(
                request
        );

        verify(company)
                .changePassword(
                        "encoded-new-password"
                );

        verify(redisTemplate)
                .delete(
                        "refreshToken:"
                                + UserRole.COMPANY.getRole()
                                + ":"
                                + COMPANY_ID
                );

        verify(redisTemplate)
                .delete(resetTokenKey);
    }

    @Test
    @DisplayName("새 비밀번호와 비밀번호 확인이 다르면 변경하지 않는다")
    void resetPasswordWithDifferentPasswordConfirmation() {
        PasswordResetRequest request =
                new PasswordResetRequest(
                        "reset-token",
                        "new-password",
                        "different-password"
                );

        assertThrows(
                CustomException.class,
                () -> passwordResetService
                        .resetPassword(
                                request
                        )
        );

        verifyNoInteractions(
                companyRepository,
                passwordEncoder,
                company
        );

        verify(
                valueOperations,
                never()
        ).get(anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰이 만료되면 비밀번호를 변경하지 않는다")
    void resetPasswordWithExpiredToken() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        String resetToken =
                "expired-token";

        String resetTokenKey =
                "verified:reset-password:company:"
                        + resetToken;

        PasswordResetRequest request =
                new PasswordResetRequest(
                        resetToken,
                        "new-password",
                        "new-password"
                );

        given(valueOperations.get(resetTokenKey))
                .willReturn(null);

        assertThrows(
                CustomException.class,
                () -> passwordResetService
                        .resetPassword(
                                request
                        )
        );

        verify(
                company,
                never()
        ).changePassword(anyString());

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("resetToken에 저장된 이메일과 현재 계정 이메일이 다르면 변경하지 않는다")
    void resetPasswordWithChangedAccountEmail() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        String resetToken =
                "reset-token";

        String resetTokenKey =
                "verified:reset-password:company:"
                        + resetToken;

        PasswordResetRequest request =
                new PasswordResetRequest(
                        resetToken,
                        "new-password",
                        "new-password"
                );

        given(valueOperations.get(resetTokenKey))
                .willReturn(
                        COMPANY_ID
                                + "|"
                                + EMAIL
                );

        given(
                companyRepository.findByIdAndStatus(
                        COMPANY_ID,
                        UserStatus.ACTIVE
                )
        ).willReturn(Optional.of(company));

        given(company.getEmail())
                .willReturn(
                        "changed-email@test.com"
                );

        assertThrows(
                CustomException.class,
                () -> passwordResetService
                        .resetPassword(
                                request
                        )
        );

        verify(
                company,
                never()
        ).changePassword(anyString());

        verify(redisTemplate)
                .delete(resetTokenKey);
    }
}