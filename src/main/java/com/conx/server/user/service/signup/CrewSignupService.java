package com.conx.server.user.service.signup;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.domain.consent.PersonalInformationConsent;
import com.conx.server.user.domain.consent.PromotionalMessageConsent;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewEvaluation;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.dto.signupRequest.SignupRequestDTO;
import com.conx.server.user.dto.signupRequest.UpdateCrewUserDTO;
import com.conx.server.user.repository.*;
import com.conx.server.user.service.common.SendingVerificationNumberService;
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
    private final PersonalInformationConsentRepository personalInformationConsentRepository;
    private final PromotionalMessageConsentRepository promotionalMessageConsentRepository;
    private final EvaluationRepository evaluationRepository;
    private final CrewEvaluationRepository crewEvaluationRepository;
    private final TokenProvider tokenProvider;

    /**
     * 회원가입 1단계
     * 인증된 이메일을 바탕으로 초기유저정보(이메일, 비밀번호 등..)를 세팅합니다.
     *
     * 에러케이스
     * 입력한 두 개의 비밀번호가 일치하지 않는 경우(S003)
     * @param req 이메일, 비밀번호, 옵션체크여부 등 로그인창에서 입력하는 정보들
     */
    @Transactional
    public void userSetting(SignupRequestDTO req) {
        req.passwordDoubleChecking();
        sendingVerificationNumberService.checkVerification(req.email());

        String password = encoder.encode(req.password());

        Crew crew = Crew.create(
                req.email(), password
        );


        PersonalInformationConsent personalInformationConsent = PersonalInformationConsent.create(crew, req.options().personalInformation());
        PromotionalMessageConsent promotionalMessageConsent = PromotionalMessageConsent.create(crew, req.options().sendingPromoteMessage());

        crewRepository.save(crew);
        personalInformationConsentRepository.save(personalInformationConsent);
        promotionalMessageConsentRepository.save(promotionalMessageConsent);
    }

    /**
     * 초기 세팅이 완료된 사용자의 추가정보(브랜드명, 담당자명 등...)를 세팅합니다.
     *
     * 에러케이스
     * 산업군/업종에 ETC(기타)를 입력하였으나 주관식 값이 입력되지 않은 경우(S002)
     * 사용자를 찾지 못한 경우(G002)
     */
    @Transactional
    public void update(UpdateCrewUserDTO req) {
        req.validateCrewType();

        Crew crew = crewRepository
                .findByEmail(req.email())
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        crew.activateCrew(
                req.crewName(),
                req.crewType(),
                req.customCrewType(),
                req.managerName(),
                req.job()
        );

        CrewEvaluation crewEvaluation = CrewEvaluation.create(crew);
        crewEvaluationRepository.save(crewEvaluation);
    }
}