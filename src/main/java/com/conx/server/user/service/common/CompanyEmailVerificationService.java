package com.conx.server.user.service.common;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.mailSender.EmailDTO;
import com.conx.server.global.mailSender.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyEmailVerificationService {

    private static final Duration CODE_EXPIRATION =
            Duration.ofMinutes(5);

    private static final Duration VERIFICATION_EXPIRATION =
            Duration.ofMinutes(30);

    private final MailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Value("${google.user}")
    private String sender;

    /**
     * 기업 계정 이메일 변경용 인증번호를 발송한다.
     */
    public void sendVerificationCode(
            Long companyId,
            String newEmail
    ) {
        String code = createVerificationCode();

        redisTemplate.opsForValue().set(
                createCodeKey(companyId, newEmail),
                code,
                CODE_EXPIRATION
        );

        EmailDTO emailDTO = createEmailDTO(
                newEmail,
                code
        );

        mailSender.sendMail(emailDTO);
    }

    /**
     * 인증번호를 확인한 뒤 이메일 변경에 사용할 일회성 토큰을 발급한다.
     */
    public String confirmVerificationCode(
            Long companyId,
            String newEmail,
            Integer inputCode
    ) {
        String codeKey =
                createCodeKey(companyId, newEmail);

        String savedCode =
                redisTemplate.opsForValue().get(codeKey);

        if (savedCode == null) {
            throw new CustomException(
                    ErrorCode.EMAIL_NOT_FOUND
            );
        }

        if (!String.valueOf(inputCode).equals(savedCode)) {
            throw new CustomException(
                    ErrorCode.CODE_UNMATCHED
            );
        }

        String verificationToken =
                UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                createVerifiedKey(
                        companyId,
                        verificationToken
                ),
                newEmail,
                VERIFICATION_EXPIRATION
        );

        redisTemplate.delete(codeKey);

        return verificationToken;
    }

    /**
     * 이메일 변경 직전에 인증 완료 토큰을 확인하고 사용 처리한다.
     */
    public void consumeVerificationToken(
            Long companyId,
            String newEmail,
            String verificationToken
    ) {
        String verifiedKey =
                createVerifiedKey(
                        companyId,
                        verificationToken
                );

        String verifiedEmail =
                redisTemplate.opsForValue().get(verifiedKey);

        if (verifiedEmail == null ||
                !verifiedEmail.equals(newEmail)) {
            throw new CustomException(
                    ErrorCode.EMAIL_CHANGE_VERIFICATION_INVALID
            );
        }

        redisTemplate.delete(verifiedKey);
    }

    private String createVerificationCode() {
        return String.valueOf(
                100000 + secureRandom.nextInt(900000)
        );
    }

    private String createCodeKey(
            Long companyId,
            String newEmail
    ) {
        return "verification:change-email:company:"
                + companyId
                + ":"
                + newEmail;
    }

    private String createVerifiedKey(
            Long companyId,
            String verificationToken
    ) {
        return "verified:change-email:company:"
                + companyId
                + ":"
                + verificationToken;
    }

    private EmailDTO createEmailDTO(
            String newEmail,
            String code
    ) {
        String subject =
                "CONX 계정 이메일 변경 인증번호";

        String text =
                "안녕하세요.\n"
                        + "CONX 계정 이메일 변경을 위한 인증번호입니다.\n"
                        + "인증번호는 "
                        + code
                        + "입니다.\n"
                        + "인증번호는 5분간 유효합니다.";

        return EmailDTO.create(
                sender,
                newEmail,
                text,
                subject
        );
    }
}