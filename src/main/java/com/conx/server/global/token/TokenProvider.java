package com.conx.server.global.token;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private Key key;

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    public TokenProvider(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token){
        getTokenLoginId(token);
    }

    public String getTokenFromHeader(HttpServletRequest req) {
        String authorization = req.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7);
    }

    public String createToken(String email, Authentication authentication, JWTType type){
        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

        int expiration = type.getValidTime() * 1000;

        return Jwts.builder()
                .subject(email)
                .expiration(new Date(new Date().getTime() + expiration))
                .claim("auth", authorities)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String getTokenLoginId(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
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

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getTokenLoginId(token));

        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
    }
}
