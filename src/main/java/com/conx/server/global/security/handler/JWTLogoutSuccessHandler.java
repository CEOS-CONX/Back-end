package com.conx.server.global.security.handler;

import com.conx.server.global.apiResponse.ApiResponse;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest req,
                                HttpServletResponse res,
                                @Nullable Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            CustomUserDetails customUserDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            redisTemplate.delete(
                    "refreshToken:" + customUserDetails.getUserEmail()
            );
        }

        SecurityContextHolder.clearContext();

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
