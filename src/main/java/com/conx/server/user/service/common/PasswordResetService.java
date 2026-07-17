package com.conx.server.user.service.common;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.mailSender.EmailDTO;
import com.conx.server.global.mailSender.MailSender;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.passwordReset.request.PasswordResetRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationConfirmRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationSendRequest;
import com.conx.server.user.dto.passwordReset.response.PasswordResetVerificationConfirmResponse;
import com.conx.server.user.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration CODE_EXPIRATION =
            Duration.ofMinutes(5);

    private static final Duration RESET_TOKEN_EXPIRATION =
            Duration.ofMinutes(30);

    private static final String VALUE_DELIMITER = "\\|";
    private static final String VALUE_SEPARATOR = "|";

    private final CompanyRepository companyRepository;
    private final MailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Value("${google.user}")
    private String sender;

    /**
     * 이름과 이메일이 일치하는 기업 계정에 인증번호를 발송한다.
     *
     * 계정 존재 여부가 외부에 노출되지 않도록,
     * 일치하는 계정이 없어도 예외를 발생시키지 않는다.
     */
    @Transactional(readOnly = true)
    public void sendVerificationCode(
            PasswordResetVerificationSendRequest request
    ) {
        String email =
                normalizeEmail(request.email());

        String name =
                normalizeName(request.name());

        Optional<Company> companyOptional =
                companyRepository
                        .findByEmailIgnoreCaseAndManagerNameAndStatus(
                                email,
                                name,
                                UserStatus.ACTIVE
                        );

        if (companyOptional.isEmpty()) {
            return;
        }

        Company company =
                companyOptional.get();

        String code =
                createVerificationCode();

        redisTemplate.opsForValue().set(
                createCodeKey(email),
                company.getId()
                        + VALUE_SEPARATOR
                        + code,
                CODE_EXPIRATION
        );

        mailSender.sendMail(
                createVerificationEmail(
                        company.getEmail(),
                        code
                )
        );
    }

    /**
     * 이메일로 전송된 인증번호를 확인하고
     * 비밀번호 재설정용 일회성 토큰을 발급한다.
     */
    public PasswordResetVerificationConfirmResponse
    confirmVerificationCode(
            PasswordResetVerificationConfirmRequest request
    ) {
        String email =
                normalizeEmail(request.email());

        String codeKey =
                createCodeKey(email);

        String savedValue =
                redisTemplate.opsForValue().get(codeKey);

        if (savedValue == null) {
            throw new CustomException(
                    ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
            );
        }

        String[] savedParts =
                savedValue.split(VALUE_DELIMITER, 2);

        if (savedParts.length != 2) {
            redisTemplate.delete(codeKey);

            throw new CustomException(
                    ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
            );
        }

        String companyId =
                savedParts[0];

        String savedCode =
                savedParts[1];

        if (!String.valueOf(request.code())
                .equals(savedCode)) {
            throw new CustomException(
                    ErrorCode.CODE_UNMATCHED
            );
        }

        String resetToken =
                UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                createResetTokenKey(resetToken),
                companyId
                        + VALUE_SEPARATOR
                        + email,
                RESET_TOKEN_EXPIRATION
        );

        redisTemplate.delete(codeKey);

        return new PasswordResetVerificationConfirmResponse(
                resetToken
        );
    }

    /**
     * 인증 완료 후 발급된 일회성 토큰을 사용해
     * 새 비밀번호를 저장한다.
     */
    @Transactional
    public void resetPassword(
            PasswordResetRequest request
    ) {
        validatePasswordRequest(request);

        String resetTokenKey =
                createResetTokenKey(
                        request.resetToken()
                );

        String verifiedValue =
                redisTemplate.opsForValue().get(
                        resetTokenKey
                );

        if (verifiedValue == null) {
            throw new CustomException(
                    ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
            );
        }

        String[] verifiedParts =
                verifiedValue.split(VALUE_DELIMITER, 2);

        if (verifiedParts.length != 2) {
            redisTemplate.delete(resetTokenKey);

            throw new CustomException(
                    ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
            );
        }

        Long companyId =
                parseCompanyId(
                        verifiedParts[0],
                        resetTokenKey
                );

        String verifiedEmail =
                verifiedParts[1];

        Company company =
                companyRepository
                        .findByIdAndStatus(
                                companyId,
                                UserStatus.ACTIVE
                        )
                        .orElseThrow(
                                () -> new CustomException(
                                        ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
                                )
                        );

        if (!company.getEmail()
                .equalsIgnoreCase(verifiedEmail)) {
            redisTemplate.delete(resetTokenKey);

            throw new CustomException(
                    ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
            );
        }

        company.changePassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        deleteRefreshToken(company);
        redisTemplate.delete(resetTokenKey);
    }

    private void validatePasswordRequest(
            PasswordResetRequest request
    ) {
        if (request.newPassword() == null ||
                request.newPassword().isBlank()) {
            throw new CustomException(
                    ErrorCode.UNFILLED_BLANK
            );
        }

        if (!Objects.equals(
                request.newPassword(),
                request.newPasswordConfirmation()
        )) {
            throw new CustomException(
                    ErrorCode.PASSWORD_DOUBLE_CHECK_FAILED
            );
        }
    }

    private Long parseCompanyId(
            String value,
            String resetTokenKey
    ) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException exception) {
            redisTemplate.delete(resetTokenKey);

            throw new CustomException(
                    ErrorCode.PASSWORD_RESET_VERIFICATION_INVALID
            );
        }
    }

    private void deleteRefreshToken(
            Company company
    ) {
        redisTemplate.delete(
                "refreshToken:"
                        + company.getRole().getRole()
                        + ":"
                        + company.getId()
        );
    }

    private String createVerificationCode() {
        return String.valueOf(
                100000
                        + secureRandom.nextInt(900000)
        );
    }

    private String createCodeKey(
            String email
    ) {
        return "verification:reset-password:company:"
                + email;
    }

    private String createResetTokenKey(
            String resetToken
    ) {
        return "verified:reset-password:company:"
                + resetToken;
    }

    private String normalizeEmail(
            String email
    ) {
        if (email == null) {
            return "";
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeName(
            String name
    ) {
        if (name == null) {
            return "";
        }

        return name.trim();
    }

    private EmailDTO createVerificationEmail(
            String email,
            String code
    ) {
        String subject =
                "CONX 비밀번호 재설정 인증번호";

        String text =
                "안녕하세요.\n"
                        + "CONX 계정 비밀번호 재설정을 위한 인증번호입니다.\n"
                        + "인증번호는 "
                        + code
                        + "입니다.\n"
                        + "인증번호는 5분간 유효합니다.";

        return EmailDTO.create(
                sender,
                email,
                text,
                subject
        );
    }
}