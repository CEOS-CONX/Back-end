package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.domain.CrewBookmark;
import com.conx.server.bookmark.repository.CrewBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.company.request.CompanyEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationConfirmRequest;
import com.conx.server.user.dto.company.request.CompanyEmailVerificationSendRequest;
import com.conx.server.user.dto.company.request.CompanyJobUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyNameUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyPasswordUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyProfileUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyRepresentativeEmailUpdateRequest;
import com.conx.server.user.dto.company.request.CompanyRepresentativePhoneUpdateRequest;
import com.conx.server.user.dto.company.response.CompanyAccountResponse;
import com.conx.server.user.dto.company.response.CompanyBookmarkedCrewResponse;
import com.conx.server.user.dto.company.response.CompanyCrewBookmarkToggleResponse;
import com.conx.server.user.dto.company.response.CompanyEmailVerificationConfirmResponse;
import com.conx.server.user.dto.company.response.CompanyProfileResponse;
import com.conx.server.user.service.common.CompanyEmailVerificationService;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Service
@RequiredArgsConstructor
public class CompanyMyPageService {

    private final UserFinder userFinder;
    private final CrewBookmarkRepository crewBookmarkRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyEmailVerificationService companyEmailVerificationService;
    private final StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public CompanyProfileResponse getProfile(Long companyId) {
        Company company =
                userFinder.findActiveCompany(companyId);

        return CompanyProfileResponse.from(company);
    }

    @Transactional
    public CompanyProfileResponse updateProfile(
            Long companyId,
            CompanyProfileUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        company.modifyProfile(
                getOrDefault(
                        request.companyName(),
                        company.getCompanyName()
                ),
                getOrDefault(
                        request.brandName(),
                        company.getBrandName()
                ),
                getOrDefault(
                        request.industry(),
                        company.getIndustry()
                ),
                getOrDefault(
                        request.customIndustry(),
                        company.getCustomIndustry()
                ),
                getOrDefault(
                        request.companyIntroduction(),
                        company.getCompanyIntroduction()
                ),
                getOrDefault(
                        request.website(),
                        company.getHomepageLink()
                ),
                getOrDefault(
                        request.additionalFileLink(),
                        company.getAdditionalFileLink()
                ),
                getOrDefault(
                        request.profileImage(),
                        company.getProfileImage()
                ),
                getOrDefault(
                        request.businessRegistrationNumber(),
                        company.getBusinessRegistrationNumber()
                )
        );

        return CompanyProfileResponse.from(company);
    }

    @Transactional(readOnly = true)
    public CompanyAccountResponse getAccount(
            Long companyId
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyAccountResponse updateName(
            Long companyId,
            CompanyNameUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        validateRequiredValue(request.name());

        company.changeManagerName(request.name());

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyAccountResponse updateJob(
            Long companyId,
            CompanyJobUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        validateRequiredValue(request.job());

        company.changeJob(request.job());

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyAccountResponse updateRepresentativePhone(
            Long companyId,
            CompanyRepresentativePhoneUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        validateRequiredValue(
                request.representativePhone()
        );

        company.changeRepresentativePhone(
                request.representativePhone()
        );

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyAccountResponse updateRepresentativeEmail(
            Long companyId,
            CompanyRepresentativeEmailUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        validateRequiredValue(
                request.representativeEmail()
        );

        company.changeRepresentativeEmail(
                request.representativeEmail()
        );

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyAccountResponse updatePassword(
            Long companyId,
            CompanyPasswordUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        validateRequiredValue(request.newPassword());

        if (!Objects.equals(
                request.newPassword(),
                request.newPasswordConfirmation()
        )) {
            throw new CustomException(
                    ErrorCode.PASSWORD_DOUBLE_CHECK_FAILED
            );
        }

        company.changePassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        deleteRefreshToken(company);

        return CompanyAccountResponse.from(company);
    }

    /**
     * 새 계정 이메일로 인증번호를 발송한다.
     */
    @Transactional(readOnly = true)
    public void sendEmailChangeVerification(
            Long companyId,
            CompanyEmailVerificationSendRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        String newEmail =
                normalizeEmail(request.newEmail());

        validateEmailChange(
                company,
                newEmail
        );

        companyEmailVerificationService
                .sendVerificationCode(
                        company.getId(),
                        newEmail
                );
    }

    /**
     * 새 이메일 인증번호를 확인하고 변경용 토큰을 발급한다.
     */
    @Transactional(readOnly = true)
    public CompanyEmailVerificationConfirmResponse
    confirmEmailChangeVerification(
            Long companyId,
            CompanyEmailVerificationConfirmRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        String newEmail =
                normalizeEmail(request.newEmail());

        validateEmailChange(
                company,
                newEmail
        );

        String verificationToken =
                companyEmailVerificationService
                        .confirmVerificationCode(
                                company.getId(),
                                newEmail,
                                request.code()
                        );

        return new CompanyEmailVerificationConfirmResponse(
                verificationToken
        );
    }

    /**
     * 인증 완료된 새 이메일로 계정 이메일을 변경한다.
     */
    @Transactional
    public CompanyAccountResponse updateEmail(
            Long companyId,
            CompanyEmailUpdateRequest request
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        verifyCurrentPassword(
                company,
                request.currentPassword()
        );

        String newEmail =
                normalizeEmail(request.newEmail());

        validateEmailChange(
                company,
                newEmail
        );

        companyEmailVerificationService
                .consumeVerificationToken(
                        company.getId(),
                        newEmail,
                        request.verificationToken()
                );

        company.changeEmail(newEmail);

        deleteRefreshToken(company);

        return CompanyAccountResponse.from(company);
    }

    @Transactional
    public CompanyCrewBookmarkToggleResponse toggleCrewBookmark(
            Long companyId,
            Long crewId
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        Crew crew =
                userFinder.findActiveCrew(crewId);

        return crewBookmarkRepository
                .findByCompanyIdAndCrewId(
                        company.getId(),
                        crew.getId()
                )
                .map(crewBookmark -> {
                    crewBookmarkRepository.delete(
                            crewBookmark
                    );

                    return CompanyCrewBookmarkToggleResponse.of(
                            crew.getId(),
                            false
                    );
                })
                .orElseGet(() -> {
                    CrewBookmark crewBookmark =
                            CrewBookmark.create(
                                    company,
                                    crew
                            );

                    crewBookmarkRepository.save(
                            crewBookmark
                    );

                    return CompanyCrewBookmarkToggleResponse.of(
                            crew.getId(),
                            true
                    );
                });
    }

    @Transactional(readOnly = true)
    public List<CompanyBookmarkedCrewResponse> getBookmarkedCrews(
            Long companyId
    ) {
        Company company =
                userFinder.findActiveCompany(companyId);

        return crewBookmarkRepository
                .findAllBookmarkedCrewResponsesByCompanyId(
                        company.getId()
                );
    }

    private void verifyCurrentPassword(
            Company company,
            String currentPassword
    ) {
        if (currentPassword == null ||
                !passwordEncoder.matches(
                        currentPassword,
                        company.getPassword()
                )) {
            throw new CustomException(
                    ErrorCode.PASSWORD_UNMATCHED
            );
        }
    }

    private void validateEmailChange(
            Company company,
            String newEmail
    ) {
        validateRequiredValue(newEmail);

        if (company.getEmail().equalsIgnoreCase(newEmail)) {
            throw new CustomException(
                    ErrorCode.EMAIL_SAME_AS_CURRENT
            );
        }

        if (userFinder.existUserByEmail(newEmail)) {
            throw new CustomException(
                    ErrorCode.EMAIL_ALREADY_IN_USE
            );
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }

        return email.trim();
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

    private void validateRequiredValue(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(
                    ErrorCode.UNFILLED_BLANK
            );
        }
    }
}