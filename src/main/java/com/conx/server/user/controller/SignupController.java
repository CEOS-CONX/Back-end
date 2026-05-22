package com.conx.server.user.controller;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.user.dto.emailKey.*;
import com.conx.server.user.dto.signupRequest.SignupRequestDTO;
import com.conx.server.user.dto.signupRequest.UpdateCompanyUserDTO;
import com.conx.server.user.dto.signupRequest.UpdateCrewUserDTO;
import com.conx.server.user.service.CompanySignupService;
import com.conx.server.user.service.CrewSignupService;
import com.conx.server.user.service.SendingVerificationNumberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SignupController {

    private final SendingVerificationNumberService sendingVerificationNumberService;
    private final CompanySignupService companySignupService;
    private final CrewSignupService crewSignupService;

    /**
     * 이메일·비밀번호를 입력받고 이메일 인증과 약관 동의를 통해 가입 절차를 진행합니다.
     * @param req 회원가입 시 입력하는 이메일, 비밀번호, 약관동의 목록 등
     * @return API명세서 반환예시 참고
     */
    @PostMapping(value = "/userinfo/company")
    public ResponseEntity<ApiResponse<?>> setCompany(
            @RequestBody SignupRequestDTO req
    ){
        return ResponseEntity.ok(companySignupService.userSetting(req));
    }

    /**
     * 기업 사용자의 이메일/비밀번호 이외의 추가 정보를 입력합니다.
     * @param req 식별용 이메일, 브랜드명, 업종, 담당자명, 직무 정보가 추가됩니다.
     * @return api 명세서 참고
     */
    @PostMapping("/usersetting/company")
    public ResponseEntity<ApiResponse<?>> updateCompany(
            @Valid @RequestBody UpdateCompanyUserDTO req
    ){
        return ResponseEntity.ok(companySignupService.update(req));
    }



    /**
     * 이메일·비밀번호를 입력받고 이메일 인증과 약관 동의를 통해 가입 절차를 진행합니다.
     * @param req 회원가입 시 입력하는 이메일, 비밀번호, 약관동의 목록 등
     * @return API명세서 반환예시 참고
     */
    @PostMapping(value = "/userinfo/crew")
    public ResponseEntity<ApiResponse<?>> setCrew(
            @RequestBody SignupRequestDTO req
    ){
        return ResponseEntity.ok(crewSignupService.userSetting(req));
    }

    /**
     * 크루 사용자의 이메일/비밀번호 이외의 추가 정보를 입력합니다.
     * 만약 이메일 인증이 되지 않았거나, 인증 후 30분이 경과하였다면 에러가 발생합니다.
     * @param req 식별용 이메일,크루명, 크루유형, 크루장명, 직무 정보가 추가됩니다.
     * @return api 명세서 참고
     */
    @PostMapping("/usersetting/crew")
    public ResponseEntity<ApiResponse<?>> updateCrew(
            @RequestBody UpdateCrewUserDTO req
    ){
        return ResponseEntity.ok(crewSignupService.update(req));
    }



    /**
     * 인증번호 발송을 요청합니다.
     * @param req 이메일 주소가 적힌 요청body 입니다.
     * @return api 명세서 참고
     */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<?>> requestEmailVerification(
            @RequestBody SendingVerificationKeyRequestDTO req
    ){
        return ResponseEntity.ok(sendingVerificationNumberService.sendCorrectKey(req.email()));
    }

    /**
     * 인증번호 발송 후, 사용자가 입력한 인증번호를 검증합니다.
     * @param req 이메일 주소 및 유저가 입력한 인증번호
     * @return api 명세서 참고
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<?>> checkEmailVerification(
            @RequestBody CheckingVerificationKeyRequestDTO req
    ){
        return ResponseEntity.ok(sendingVerificationNumberService.checkCorrectKey(req));
    }

}
