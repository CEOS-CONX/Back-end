package com.conx.server.Global.Config;

import com.conx.server.Global.Security.JWTAuthenticationFilter;
import com.conx.server.Global.Security.JWTLoginFilter;
import com.conx.server.Global.Security.JWTLoginSuccessHandler;
import com.conx.server.Global.Security.JWTLogoutSuccessHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConfig {
    private final JWTLoginFilter jwtLoginFilter;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final JWTLoginSuccessHandler jwtLoginSuccessHandler;
    private final JWTLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        jwtLoginFilter.setFilterProcessesUrl("/api/login");
        jwtLoginFilter.setAuthenticationSuccessHandler(jwtLoginSuccessHandler);

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
                        jwtLoginFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(jwtLogoutSuccessHandler)
                );

        return http.build();
    }

}
