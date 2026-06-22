package com.conx.server.user.service.login_logout;

import com.conx.server.global.security.userDetails.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LogoutService {

    private final RedisTemplate<String, String> redisTemplate;

    public void logout(CustomUserDetails customUserDetails){
        redisTemplate.delete(
                "refreshToken:" + customUserDetails.getUserEmail()
        );
    }
}
