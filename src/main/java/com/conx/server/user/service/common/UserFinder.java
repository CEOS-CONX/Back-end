package com.conx.server.user.service.common;

import com.conx.server.global.common.EmailNormalizer;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.admin.Admin;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.repository.AdminRepository;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.user.repository.CrewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFinder {

    private final CrewRepository crewRepository;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public User findByEmail(String email) {
        String normalizedEmail = EmailNormalizer.normalize(email);

        return companyRepository.findByEmail(normalizedEmail)
                .map(User.class::cast)
                .orElseGet(() ->
                        crewRepository.findByEmail(normalizedEmail)
                                .map(User.class::cast)
                                .orElseGet(() ->
                                        adminRepository.findByEmail(normalizedEmail).orElseThrow(() ->
                                                new CustomException(ErrorCode.USER_NOT_FOUND)
                                        )
                                )
                );
    }

    @Transactional
    public User findActiveUserByEmail(String email) {
        return companyRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .map(User.class::cast)
                .orElseGet(() ->
                        crewRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                                .map(User.class::cast)
                                .orElseGet(() ->
                                        adminRepository.findByEmailAndStatus(email, UserStatus.ACTIVE).orElseThrow(() ->
                                                new CustomException(ErrorCode.USER_NOT_FOUND)
                                        )
                                )
                );
    }

    @Transactional(readOnly = true)
    public boolean existUserByEmail(String email) {
        if(companyRepository.existsByEmail(email)){
            return true;
        } else return crewRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean informationIsFilled(User user){
        if (user instanceof Admin admin){
            return true;
        }

        if (user instanceof Crew crew){
            return !crew.getSchools().isEmpty();
        } else if (user instanceof Company company){
            return company.getCompanyIntroduction() != null;
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Transactional
    public Crew findActiveCrew(long id){
        return crewRepository.findByIdAndStatus(id, UserStatus.ACTIVE).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public Optional<User> findOptionalActiveUserByEmail(String email) {
        String normalizedEmail = EmailNormalizer.normalize(email);

        return companyRepository.findByEmailAndStatus(normalizedEmail, UserStatus.ACTIVE)
                .map(User.class::cast)
                .or(() -> crewRepository.findByEmailAndStatus(normalizedEmail, UserStatus.ACTIVE)
                        .map(User.class::cast))
                .or(() -> adminRepository.findByEmailAndStatus(normalizedEmail, UserStatus.ACTIVE)
                        .map(User.class::cast));
    }

    @Transactional
    public Company findActiveCompany(long id){
        return companyRepository.findByIdAndStatus(id, UserStatus.ACTIVE).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public Admin findAdmin(long id){
        return adminRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
