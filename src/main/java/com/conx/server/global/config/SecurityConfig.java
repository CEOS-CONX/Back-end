package com.conx.server.global.config;

import com.conx.server.global.security.filter.JWTAuthenticationFilter;
import com.conx.server.global.security.filter.JWTLoginFilter;
import com.conx.server.global.security.handler.JWTLoginSuccessHandler;
import com.conx.server.global.security.handler.JWTLogoutSuccessHandler;
import com.conx.server.global.token.TokenProvider;
import com.conx.server.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.h2.command.Token;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTLoginSuccessHandler jwtLoginSuccessHandler;
    private final JWTLogoutSuccessHandler jwtLogoutSuccessHandler;

    public JWTLoginFilter jwtLoginFilter(UserRepository userRepository, PasswordEncoder passwordEncoder){
        JWTLoginFilter jwtLoginFilter = new JWTLoginFilter(userRepository, passwordEncoder);

        jwtLoginFilter.setFilterProcessesUrl("/api/login");
        jwtLoginFilter.setAuthenticationSuccessHandler(jwtLoginSuccessHandler);

        return jwtLoginFilter;
    }

    public JWTAuthenticationFilter jwtAuthenticationFilter(TokenProvider tokenProvider){
        return new JWTAuthenticationFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            UserRepository userRepository,
                                            PasswordEncoder passwordEncoder,
                                            TokenProvider tokenProvider) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/health/**"
                        ).permitAll()
                        .requestMatchers(
                                "/",
                                "/css/**", "/images/**", "/favicon.ico/**",
                                "/api/auth/**",
                                "/api/login/**",
                                "/api/email/**"
                        ).permitAll()
                        .anyRequest().authenticated())

                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .csrf(AbstractHttpConfigurer::disable)
                //테스트 끝나고 켜놓기

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .addFilterAt(
                        jwtLoginFilter(userRepository, passwordEncoder),
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(
                        jwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(jwtLogoutSuccessHandler)
                );

        return http.build();
    }

}
