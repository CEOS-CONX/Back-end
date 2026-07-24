package com.conx.server.user.service.login_logout;

import com.conx.server.global.exception.CustomAuthenticationException;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.global.token.JWTType;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.domain.User;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.login.request.LoginRequestDTO;
import com.conx.server.user.dto.login.response.LoginServiceResponseDTO;
import com.conx.server.user.dto.login.response.TokenReissueResponseDTO;
import com.conx.server.user.service.common.UserFinder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginService {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserFinder userFinder;
    private final RedisTemplate<String, String> redisTemplate;

    //이메일 입력 → 인증번호 발송 → 이메일+비밀번호 입력

    /**
     * 로그인 로직을 처리합니다.
     *
     * 에러케이스
     * 가입되지 않은 이메일인 경우(G002)
     * 비밀번호가 일치하지 않는 경우(A001)
     * @param req 로그인 유저정보. 이메일과 비밀번호로 구성
     * @return
     */
    @Transactional(readOnly = true)
    public LoginServiceResponseDTO login(LoginRequestDTO req){
        User user = userFinder.findByEmail(req.email());

        if(!user.isLoginable()){
            throw new CustomException(ErrorCode.INVALID_USER_TYPE);
        }

        if (!passwordEncoder.matches(req.password(), user.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_UNMATCHED);
        }

        CustomUserDetails userDetails = CustomUserDetails.of(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String accessToken = tokenProvider.createToken(userDetails.getId(), authentication, JWTType.ACCESS);
        String refreshToken = tokenProvider.createToken(userDetails.getId(), authentication, JWTType.REFRESH);
        boolean hasFullInfo = userFinder.informationIsFilled(user);

        redisTemplate.opsForValue().set(
                "refreshToken:" + user.getRole().getRole() + ":" + user.getId(),
                refreshToken,
                JWTType.REFRESH.getValidTime(),
                TimeUnit.SECONDS
        );

        return LoginServiceResponseDTO.create(accessToken, refreshToken, hasFullInfo,
                user.getId(), user.getEmail(), user.getRole());
    }

    /**
     * accessToken이 만료된 경우 refreshToken을 토대로 accessToken을 재발급합니다.
     *
     * 에러케이스
     * refreshToken이 만료된 경우(T001)
     * 잘못된 형식의 토큰인 경우(T002)
     * JWT토큰의 서명이 위조된 경우(T003)
     * 기타 토큰에 문제가 발생한 경우(T004)
     * Redis에 저장된 토큰이 없는 경우(T005)
     * Redis에 저장된 토큰과 쿠키의 토큰이 다른 경우(A002, Redis에서 refreshToken이 제거됩니다)
     *
     * @param refreshToken 리프레시 토큰
     */
    @Transactional(readOnly = true)
    public TokenReissueResponseDTO reIssueToken(String refreshToken){
        long userId = tokenProvider.getIdFromToken(refreshToken);
        List<String> roles = tokenProvider.getRoleFromToken(refreshToken);
        User user;

        boolean isCrew = roles.contains(UserRole.CREW.getRole());
        boolean isCompany = roles.contains(UserRole.COMPANY.getRole());

        if (isCrew == isCompany) {
            throw new CustomAuthenticationException(ErrorCode.INVALID_USER_TYPE);
        }

        if (roles.contains(UserRole.CREW.getRole())){
            user = userFinder.findActiveCrew(userId);
        } else if(roles.contains(UserRole.COMPANY.getRole())) {
            user = userFinder.findActiveCompany(userId);
        } else if(roles.contains(UserRole.ADMIN.getRole())) {
            user = userFinder.findAdmin(userId);
        } else {
            throw new CustomAuthenticationException(ErrorCode.INVALID_USER_TYPE);
        }

        String role = user.getRole().getRole();
        String refreshTokenInRedis = redisTemplate.opsForValue().get(
                "refreshToken:" + role + ":" + userId
        );

        if (refreshTokenInRedis == null){
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if (!refreshToken.equals(refreshTokenInRedis)){
            redisTemplate.delete("refreshToken:" + role + ":" + userId);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        CustomUserDetails userDetails = CustomUserDetails.of(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String accessToken = tokenProvider.createToken(userDetails.getId(), authentication, JWTType.ACCESS);
        String newRefreshToken = tokenProvider.createToken(userDetails.getId(), authentication, JWTType.REFRESH);

        redisTemplate.opsForValue().set(
                "refreshToken:" + role + ":" + user.getId(),
                newRefreshToken,
                JWTType.REFRESH.getValidTime(),
                TimeUnit.SECONDS
        );

        return new TokenReissueResponseDTO(accessToken, newRefreshToken);
    }
}