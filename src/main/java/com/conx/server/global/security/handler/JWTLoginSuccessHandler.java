package com.conx.server.global.security.handler;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.global.token.JWTType;
import com.conx.server.global.token.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = tokenProvider.createToken(userDetails.getUserEmail(), authentication, JWTType.ACCESS);
        String refreshToken = tokenProvider.createToken(userDetails.getUserEmail(), authentication, JWTType.REFRESH);

        res.addHeader(
                "Authorization", "Bearer " + accessToken
        );
        redisTemplate.opsForValue().set(
                "refreshToken:" + userDetails.getUserEmail(),
                refreshToken
        );

        res.setStatus(200);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.ofEmpty()
                )
        );
    }
}
