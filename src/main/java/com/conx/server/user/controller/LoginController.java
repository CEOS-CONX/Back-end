package com.conx.server.user.controller;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginResponseDTO;
import com.conx.server.user.dto.login.response.LoginServiceResponseDTO;
import com.conx.server.user.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @RequestBody LoginRequestDTO req,
            HttpServletResponse res
    ){
        LoginServiceResponseDTO responseDTO = loginService.login(req);
        String accessToken = responseDTO.accessToken();

        res.addHeader(
                "Authorization", "Bearer " + accessToken
        );

        return ResponseEntity.ok(ApiResponse.of("성공", LoginResponseDTO.create(responseDTO), Page.empty()));
    }
}
