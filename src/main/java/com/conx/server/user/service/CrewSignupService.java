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
import com.conx.server.user.dto.signupRequest.UpdateCrewUserDTO;
import com.conx.server.user.repository.CrewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewSignupService {

    private final CrewRepository crewRepository;
    private final PasswordEncoder encoder;
    private final SendingVerificationNumberService sendingVerificationNumberService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public ApiResponse<?> userSetting(SignupRequestDTO req){

        if(crewRepository.existsByEmail(req.email())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXITS);
        }

        String password = encoder.encode(req.password());

        Crew crew = Crew.create(
                req.email(), password
        );

        crewRepository.save(crew);
        return ApiResponse.ofEmpty();
    }

    @Transactional
    public ApiResponse<?> update(UpdateCrewUserDTO req){

        req.validateCrewType();

        Crew crew = crewRepository.findByEmail(req.email()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        crew.activateCrew(
                req.crewName(), req.crewType(), req.customCrewType(), req.managerName(), req.job()
        );

        sendingVerificationNumberService.checkVerification(req.email());
        return ApiResponse.ofEmpty();
    }
}
