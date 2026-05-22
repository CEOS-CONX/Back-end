package com.conx.server.user.service;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.User;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.signupRequest.SignupRequestDTO;
import com.conx.server.user.dto.signupRequest.UpdateCompanyUserDTO;
import com.conx.server.user.repository.CompanyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanySignupService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder encoder;
    private final SendingVerificationNumberService sendingVerificationNumberService;

    @Transactional
    public ApiResponse<?> userSetting(SignupRequestDTO req){

        if(companyRepository.existsByEmail(req.email())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXITS);
        }

        String password = encoder.encode(req.password());

        Company company = Company.create(
                req.email(), password
        );

        companyRepository.save(company);
        return ApiResponse.ofEmpty();
    }

    @Transactional
    public ApiResponse<?> update(UpdateCompanyUserDTO req){

        req.validateIndustry();

        Company company = companyRepository.findByEmail(req.email()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        company.activateCompany(req.brandName(), req.industry(), req.customIndustry(),
                req.managerName(), req.job());


        sendingVerificationNumberService.checkVerification(req.email());
        return ApiResponse.ofEmpty();
    }
}
