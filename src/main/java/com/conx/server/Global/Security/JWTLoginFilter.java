package com.conx.server.Global.Security;

import com.conx.server.User.DTO.LoginRequest.LoginRequestDTO;
import com.conx.server.User.Domain.User;
import com.conx.server.User.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JWTLoginFilter(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            LoginRequestDTO requestDTO = objectMapper.readValue(req.getInputStream(), LoginRequestDTO.class);
            User user = userRepository.findByEmail(requestDTO.email()).orElseThrow(
                    //TODO: CustomException
                    () -> new RuntimeException("유저가 없습니다.")
            );

            if (!passwordEncoder.matches(requestDTO.password(), user.getPassword())){
                //TODO: CustomException
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }

            UserDetails userDetails = CustomUserDetails.of(user);
            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

        } catch (IOException e) {
            //TODO: CustomException
            throw new RuntimeException(e);
        }
    }
}
