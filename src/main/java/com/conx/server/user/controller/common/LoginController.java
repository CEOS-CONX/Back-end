package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponse;
import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginResponseDTO;
import com.conx.server.user.dto.login.response.LoginServiceResponseDTO;
import com.conx.server.user.dto.login.response.TokenReissueResponseDTO;
import com.conx.server.user.service.login_logout.LoginService;
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
