package com.conx.server.user.service.login_logout;

import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.dto.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    @DisplayName("로그아웃 시 역할과 사용자 ID로 저장된 Refresh Token을 삭제한다")
    void logout() {
        given(customUserDetails.getId())
                .willReturn(USER_ID);

        given(customUserDetails.getUserRole())
                .willReturn(UserRole.COMPANY.getRole());

        logoutService.logout(customUserDetails);

        verify(redisTemplate).delete(
                "refreshToken:"
                        + UserRole.COMPANY.getRole()
                        + ":"
                        + USER_ID
        );
    }
}