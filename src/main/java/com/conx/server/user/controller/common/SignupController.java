package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.dto.emailKey.*;
import com.conx.server.user.dto.signupRequest.SignupRequestDTO;
import com.conx.server.user.dto.signupRequest.UpdateCompanyUserDTO;
import com.conx.server.user.dto.signupRequest.UpdateCrewUserDTO;
import com.conx.server.user.service.signup.CompanySignupService;
import com.conx.server.user.service.signup.CrewSignupService;
import com.conx.server.user.service.login_logout.LoginService;
import com.conx.server.user.service.common.SendingVerificationNumberService;
import io.swagger.v3.oas.annotations.Operation;
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
    private final LoginService loginService;
    private final TokenProvider tokenProvider;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 인증번호 발송을 요청합니다.
     * @param req 이메일 주소가 적힌 요청body 입니다.
     * @return api 명세서 참고
     */
    @Operation(
            summary = "회원가입 이메일 인증번호 발송",
            description = "가입할 이메일로 6자리 인증번호를 발송하며 인증번호는 5분간 유효합니다. Company 또는 Crew에 이미 등록된 이메일에는 발송되지 않으며 email은 필수입니다."
    )
    @PostMapping("/email/send")
    public ApiResponse<?> requestEmailVerification(
            @Valid @RequestBody SendingVerificationKeyRequestDTO req
    ){
        sendingVerificationNumberService.sendCorrectKey(req.email());
        return apiResponseFactory.success("인증번호 발송에 성공했습니다.", null);
    }

    /**
     * 인증번호 발송 후, 사용자가 입력한 인증번호를 검증합니다.
     * @param req 이메일 주소 및 유저가 입력한 인증번호
     * @return api 명세서 참고
     */
    @Operation(
            summary = "회원가입 이메일 인증번호 확인",
            description = "이메일과 6자리 인증번호를 확인하고 30분간 유효한 회원가입 인증 완료 정보를 저장합니다. 성공 후 인증번호는 삭제되며, 30분 안에 기업 또는 크루 계정 생성 API를 호출해야 합니다."
    )
    @PostMapping("/email/verify")
    public ApiResponse<?> checkEmailVerification(
            @Valid @RequestBody CheckingVerificationKeyRequestDTO req
    ){
        sendingVerificationNumberService.checkCorrectKey(req);
        return apiResponseFactory.success("인증번호 인증에 성공했습니다.", null);
    }


    /**
     * 이메일·비밀번호를 입력받고 이메일 인증과 약관 동의를 통해 가입 절차를 진행합니다.
     * @param req 회원가입 시 입력하는 이메일, 비밀번호, 약관동의 목록 등
     * @return API명세서 반환예시 참고
     */
    @Operation(
            summary = "기업 계정 기본 정보 등록",
            description = "이메일 인증 후 30분 안에 이메일, 비밀번호·확인값과 약관 동의값을 제출하여 PENDING 기업 계정을 생성합니다. 비밀번호는 서버에서 암호화되며 이 단계에서는 로그인 토큰이 발급되지 않습니다."
    )
    @PostMapping(value = "/userinfo/company")
    public ApiResponse<?> setCompany(
            @Valid @RequestBody SignupRequestDTO req
    ){
        companySignupService.userSetting(req);
        return apiResponseFactory.success("초기 사용자 정보 입력에 성공했습니다.(기업)", null);
    }

    /**
     * 기업 사용자의 이메일/비밀번호 이외의 추가 정보를 입력합니다.
     * @param req 식별용 이메일, 브랜드명, 업종, 담당자명, 직무 정보가 추가됩니다.
     * @return api 명세서 참고
     */
    @Operation(
            summary = "기업 회원가입 추가 정보 등록",
            description = "기본 정보로 생성된 기업 계정에 브랜드명, 업종, 담당자명과 직무를 저장하고 계정을 활성화합니다. industry는 대문자 enum 값이며 ETC 선택 시 customIndustry가 필요하고, 이 API 자체는 이메일 인증을 다시 확인하지 않습니다."
    )
    @PostMapping("/usersetting/company")
    public ApiResponse<?> updateCompany(
            @Valid @RequestBody UpdateCompanyUserDTO req
    ){
        companySignupService.update(req);
        return apiResponseFactory.success("사용자 정보 추가 입력에 성공했습니다.(기업)", null);
    }


    /**
     * 이메일·비밀번호를 입력받고 이메일 인증과 약관 동의를 통해 가입 절차를 진행합니다.
     * @param req 회원가입 시 입력하는 이메일, 비밀번호, 약관동의 목록 등
     * @return API명세서 반환예시 참고
     */
    @Operation(
            summary = "크루 계정 기본 정보 등록",
            description = "이메일 인증 후 30분 안에 이메일, 비밀번호·확인값과 약관 동의값을 제출하여 PENDING 크루 계정을 생성합니다. 비밀번호는 서버에서 암호화되며 이 단계에서는 로그인 토큰이 발급되지 않습니다."
    )
    @PostMapping(value = "/userinfo/crew")
    public ApiResponse<?> setCrew(
            @Valid @RequestBody SignupRequestDTO req
    ){
        crewSignupService.userSetting(req);
        return apiResponseFactory.success("초기 사용자 정보 입력에 성공했습니다.(크루)", null);
    }

    /**
     * 크루 사용자의 이메일/비밀번호 이외의 추가 정보를 입력합니다.
     * 만약 이메일 인증이 되지 않았거나, 인증 후 30분이 경과하였다면 에러가 발생합니다.
     * @param req 식별용 이메일,크루명, 크루유형, 크루장명, 직무 정보가 추가됩니다.
     * @return api 명세서 참고
     */
    @Operation(
            summary = "크루 회원가입 추가 정보 등록",
            description = "기본 정보로 생성된 크루 계정에 크루명, 유형, 담당자명과 직무를 저장하고 계정을 활성화합니다. crewType은 대문자 enum 값이며 ETC 선택 시 customCrewType이 필요하고, 이 API 자체는 이메일 인증을 다시 확인하지 않습니다."
    )
    @PostMapping("/usersetting/crew")
    public ApiResponse<?> updateCrew(
            @Valid @RequestBody UpdateCrewUserDTO req
    ){
        crewSignupService.update(req);
        return apiResponseFactory.success("사용자 정보 추가 입력에 성공했습니다.(크루)", null);
    }
}
