package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginResponseDTO;
import com.conx.server.user.dto.login.response.LoginServiceResponseDTO;
import com.conx.server.user.dto.login.response.TokenReissueResponseDTO;
import com.conx.server.user.service.login_logout.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginController {

    private final LoginService loginService;
    private final TokenProvider tokenProvider;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * 로그인 API입니다.
     * @param req 이메일 및 비밀번호
     * @param res HttpServletResponse
     */
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 COMPANY, CREW 또는 ADMIN 계정에 로그인합니다. access token은 Authorization 응답 헤더에, 3일간 유효한 refresh token은 HttpOnly·SameSite=Strict 쿠키에 저장됩니다."
    )
    @PostMapping
    public ApiResponse<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO req,
            HttpServletResponse res
    ){
        LoginServiceResponseDTO responseDTO = loginService.login(req);
        tokenProvider.setToken(responseDTO.accessToken(), responseDTO.refreshToken(), res);

        return apiResponseFactory.success("로그인에 성공했습니다.", LoginResponseDTO.create(responseDTO), null);
    }

    /**
     * 인증 요청 시 accessToken이 만료된 경우 이 API를 호출하여 새 accessToken을 발급받야아합니다.
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     */
    @Operation(
            summary = "로그인 토큰 재발급",
            description = "refreshToken 쿠키를 검증하여 COMPANY 또는 CREW의 access token과 refresh token을 모두 새로 발급합니다. 새 access token은 Authorization 응답 헤더에, 회전된 refresh token은 쿠키에 저장되며 Redis 유효시간도 3일로 갱신됩니다."
    )
    @PostMapping("/refresh")
    public ApiResponse<?> reIssueAccessToken(HttpServletRequest req,
                                             HttpServletResponse res){
        TokenReissueResponseDTO responseDTO = loginService.reIssueToken(tokenProvider.getTokenFromCookie(req));
        tokenProvider.setToken(responseDTO.accessToken(),
                responseDTO.refreshToken(),
                res);

        return apiResponseFactory.success(null);
    }
}
