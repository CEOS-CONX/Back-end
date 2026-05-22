package com.conx.server.user.service;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.global.token.JWTType;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.domain.User;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginServiceResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginService {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserFinder userFinder;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginServiceResponseDTO login(LoginRequestDTO req){
        User user = userFinder.findByEmail(req.email());

        if (!passwordEncoder.matches(req.password(), user.getPassword())){
            //TODO: 로그
            throw new CustomException(ErrorCode.PASSWORD_UNMATCHED);
        }

        UserDetails userDetails = CustomUserDetails.of(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String accessToken = tokenProvider.createToken(req.email(), authentication, JWTType.ACCESS);
        String refreshToken = tokenProvider.createToken(req.email(), authentication, JWTType.REFRESH);
        boolean hasFullInfo = userFinder.informationIsFilled(user);

        redisTemplate.opsForValue().set(
                "refreshToken:" + user.getEmail(),
                refreshToken,
                JWTType.REFRESH.getValidTime(),
                TimeUnit.SECONDS
        );

        return LoginServiceResponseDTO.create(accessToken, hasFullInfo, user.getId(), user.getEmail(), user.getRole());
    }
}