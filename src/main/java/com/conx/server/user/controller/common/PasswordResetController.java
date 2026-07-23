package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.user.dto.passwordReset.request.PasswordResetRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationConfirmRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationSendRequest;
import com.conx.server.user.dto.passwordReset.response.PasswordResetVerificationConfirmResponse;
import com.conx.server.user.service.common.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 이름과 가입 이메일을 확인하고 인증번호 발송을 요청한다.
     *
     * 계정 존재 여부와 관계없이 동일한 성공 메시지를 반환한다.
     */
    @Operation(
            summary = "기업 비밀번호 재설정 인증번호 발송",
            description = "ACTIVE 기업 계정의 담당자명과 가입 이메일이 일치하면 5분간 유효한 6자리 인증번호를 발송합니다. 계정 존재 여부 보호를 위해 일치하는 계정이 없어도 동일한 성공 응답을 반환합니다."
    )
    @PostMapping("/verifications")
    public ApiResponse<?> sendVerificationCode(
            @Valid
            @RequestBody
            PasswordResetVerificationSendRequest request
    ) {
        passwordResetService.sendVerificationCode(
                request
        );

        return apiResponseFactory.success(
                "입력한 정보와 일치하는 계정이 있으면 인증번호가 발송됩니다.",
                null
        );
    }

    /**
     * 이메일 인증번호를 확인하고 비밀번호 재설정 토큰을 발급한다.
     */
    @Operation(
            summary = "기업 비밀번호 재설정 인증번호 확인",
            description = "가입 이메일과 6자리 인증번호를 확인하고 30분간 유효한 일회성 resetToken을 응답 본문에 발급합니다. 확인 성공 후 기존 인증번호는 삭제됩니다."
    )
    @PostMapping("/verifications/confirm")
    public ApiResponse<PasswordResetVerificationConfirmResponse>
    confirmVerificationCode(
            @Valid
            @RequestBody
            PasswordResetVerificationConfirmRequest request
    ) {
        PasswordResetVerificationConfirmResponse response =
                passwordResetService.confirmVerificationCode(
                        request
                );

        return apiResponseFactory.success(
                "비밀번호 재설정 이메일 인증에 성공했습니다.",
                response,
                null
        );
    }

    /**
     * 인증 완료 토큰을 사용해 비밀번호를 재설정한다.
     */
    @Operation(
            summary = "기업 비밀번호 재설정",
            description = "인증번호 확인 단계에서 발급된 30분 유효 resetToken과 새 비밀번호·확인값으로 기업 비밀번호를 변경합니다. 성공 시 resetToken과 Redis refresh token은 삭제되지만 기존 access token과 클라이언트 쿠키는 즉시 삭제되지 않습니다."
    )
    @PatchMapping
    public ApiResponse<?> resetPassword(
            @Valid
            @RequestBody
            PasswordResetRequest request
    ) {
        passwordResetService.resetPassword(
                request
        );

        return apiResponseFactory.success(
                "비밀번호가 재설정되었습니다.",
                null
        );
    }
}
