package com.conx.server.user.service.common;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.mailSender.EmailDTO;
import com.conx.server.global.mailSender.MailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyEmailVerificationServiceTest {

    private static final Long COMPANY_ID = 1L;

    private static final String NEW_EMAIL =
            "new-company@test.com";

    private static final String CODE_KEY =
            "verification:change-email:company:"
                    + COMPANY_ID
                    + ":"
                    + NEW_EMAIL;

    @Mock
    private MailSender mailSender;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CompanyEmailVerificationService
            companyEmailVerificationService;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
    }

    @Test
    @DisplayName("이메일 변경 인증번호를 Redis에 저장하고 메일로 발송한다")
    void sendVerificationCode() {
        companyEmailVerificationService
                .sendVerificationCode(
                        COMPANY_ID,
                        NEW_EMAIL
                );

        verify(valueOperations).set(
                eq(CODE_KEY),
                matches("\\d{6}"),
                eq(Duration.ofMinutes(5))
        );

        ArgumentCaptor<EmailDTO> emailCaptor =
                ArgumentCaptor.forClass(
                        EmailDTO.class
                );

        verify(mailSender)
                .sendMail(emailCaptor.capture());

        EmailDTO emailDTO =
                emailCaptor.getValue();

        assertEquals(
                NEW_EMAIL,
                emailDTO.getReceiver()
        );

        assertEquals(
                "CONX 계정 이메일 변경 인증번호",
                emailDTO.getSubject()
        );
    }

    @Test
    @DisplayName("인증번호가 일치하면 이메일 변경용 일회성 토큰을 발급한다")
    void confirmVerificationCode() {
        given(valueOperations.get(CODE_KEY))
                .willReturn("123456");

        String verificationToken =
                companyEmailVerificationService
                        .confirmVerificationCode(
                                COMPANY_ID,
                                NEW_EMAIL,
                                123456
                        );

        assertFalse(
                verificationToken.isBlank()
        );

        verify(valueOperations).set(
                eq(
                        "verified:change-email:company:"
                                + COMPANY_ID
                                + ":"
                                + verificationToken
                ),
                eq(NEW_EMAIL),
                eq(Duration.ofMinutes(30))
        );

        verify(redisTemplate)
                .delete(CODE_KEY);
    }

    @Test
    @DisplayName("이메일 변경 인증번호가 일치하지 않으면 예외가 발생한다")
    void confirmVerificationCodeWithWrongCode() {
        given(valueOperations.get(CODE_KEY))
                .willReturn("123456");

        assertThrows(
                CustomException.class,
                () -> companyEmailVerificationService
                        .confirmVerificationCode(
                                COMPANY_ID,
                                NEW_EMAIL,
                                999999
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

        verify(
                redisTemplate,
                never()
        ).delete(CODE_KEY);
    }

    @Test
    @DisplayName("인증번호가 만료되면 예외가 발생한다")
    void confirmExpiredVerificationCode() {
        given(valueOperations.get(CODE_KEY))
                .willReturn(null);

        assertThrows(
                CustomException.class,
                () -> companyEmailVerificationService
                        .confirmVerificationCode(
                                COMPANY_ID,
                                NEW_EMAIL,
                                123456
                        )
        );
    }

    @Test
    @DisplayName("인증 완료 토큰을 확인하고 일회성으로 소비한다")
    void consumeVerificationToken() {
        String verificationToken =
                "verification-token";

        String verifiedKey =
                "verified:change-email:company:"
                        + COMPANY_ID
                        + ":"
                        + verificationToken;

        given(valueOperations.get(verifiedKey))
                .willReturn(NEW_EMAIL);

        companyEmailVerificationService
                .consumeVerificationToken(
                        COMPANY_ID,
                        NEW_EMAIL,
                        verificationToken
                );

        verify(redisTemplate)
                .delete(verifiedKey);
    }

    @Test
    @DisplayName("인증한 이메일과 변경하려는 이메일이 다르면 토큰을 소비하지 않는다")
    void consumeVerificationTokenWithDifferentEmail() {
        String verificationToken =
                "verification-token";

        String verifiedKey =
                "verified:change-email:company:"
                        + COMPANY_ID
                        + ":"
                        + verificationToken;

        given(valueOperations.get(verifiedKey))
                .willReturn(
                        "different@test.com"
                );

        assertThrows(
                CustomException.class,
                () -> companyEmailVerificationService
                        .consumeVerificationToken(
                                COMPANY_ID,
                                NEW_EMAIL,
                                verificationToken
                        )
        );

        verify(
                redisTemplate,
                never()
        ).delete(verifiedKey);
    }

    @Test
    @DisplayName("이미 소비되었거나 만료된 이메일 변경 토큰은 사용할 수 없다")
    void consumeExpiredVerificationToken() {
        String verificationToken =
                "expired-token";

        String verifiedKey =
                "verified:change-email:company:"
                        + COMPANY_ID
                        + ":"
                        + verificationToken;

        given(valueOperations.get(verifiedKey))
                .willReturn(null);

        assertThrows(
                CustomException.class,
                () -> companyEmailVerificationService
                        .consumeVerificationToken(
                                COMPANY_ID,
                                NEW_EMAIL,
                                verificationToken
                        )
        );
    }
}