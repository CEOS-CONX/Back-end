package com.conx.server.global.token;

import com.conx.server.global.exception.CustomAuthenticationException;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.user.domain.User;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.service.common.UserFinder;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private final UserFinder userFinder;
    private Key key;

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    public TokenProvider(UserFinder userFinder){
        this.userFinder = userFinder;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getTokenFromHeader(HttpServletRequest req) {
        String authorization = req.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7);
    }

    public String getTokenFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                return c.getValue();
            }
        }

        return null;
    }

    public String createToken(long userId, Authentication authentication, JWTType type){
        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        int expiration = type.getValidTime() * 1000;

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(new Date().getTime() + expiration))
                .claim("auth", authorities)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public long getIdFromToken(String token) {
        Claims claims = getClaim(token);
        return Long.parseLong(claims.getSubject());
    }


    private Claims getClaim(String token){
        try{
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException eje){
            //토큰만료
            //TODO: 로그
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException mje){
            //잘못된 형식
            //TODO: 로그
            throw new CustomException(ErrorCode.INVALID_TOKEN_FORM);
        } catch (SignatureException se){
            //위조된 시그니처
            //TODO: 로그
            throw new CustomException(ErrorCode.INVALID_SIGNATURE);
        } catch (JwtException je){
            //기타
            //TODO: 로그
            throw new CustomException(ErrorCode.INTERNAL_TOKEN_ERROR);
        }
    }

    public List<String> getRoleFromToken(String token) {
        Claims claims = getClaim(token);
        String auth = claims.get("auth", String.class);

        return Arrays.asList(auth.split(","));
    }

    private void setTokenInHeader(String token, HttpServletResponse res){
        res.addHeader(
                "Authorization", "Bearer " + token
        );
    }

    private void setTokenInCookie(String token, HttpServletResponse res){
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setMaxAge(JWTType.REFRESH.getValidTime());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Strict");
        //cookie.setSecure(true);
        //TODO: https 배포 시 주석 제거

        res.addCookie(cookie);
    }

    public void setToken(String accessToken,
                         String refreshToken,
                         HttpServletResponse res){
        setTokenInHeader(accessToken, res);
        setTokenInCookie(refreshToken, res);
    }

    public Authentication getAuthentication(String token) {
        long userId = getIdFromToken(token);
        List<String> roles = getRoleFromToken(token);
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
        } else {
            throw new CustomAuthenticationException(ErrorCode.INVALID_USER_TYPE);
        }

        UserDetails userDetails = CustomUserDetails.of(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
    }
}
