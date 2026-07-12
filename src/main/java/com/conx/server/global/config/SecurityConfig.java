package com.conx.server.global.config;

import com.conx.server.global.security.filter.JWTAuthenticationFilter;
import com.conx.server.global.security.handler.CustomAccessDeniedHandler;
import com.conx.server.global.security.handler.JWTAuthenticationEntryPoint;
import com.conx.server.global.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(
            TokenProvider tokenProvider
    ) {
        return new JWTAuthenticationFilter(
                tokenProvider
        );
    }

    @Bean
    public JWTAuthenticationEntryPoint
    jwtAuthenticationEntryPoint() {
        return new JWTAuthenticationEntryPoint();
    }

    @Bean
    public CustomAccessDeniedHandler
    customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JWTAuthenticationFilter jwtAuthenticationFilter,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint
    ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/",
                                "/css/**",
                                "/images/**",
                                "/favicon.ico/**",
                                "/api/v1/auth/**",
                                "/api/v1/login/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/health/**"
                        ).permitAll()

                        // 비로그인 랜딩
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/landing"
                        ).anonymous()

                        // 프로젝트/크루 목록만 비로그인 허용
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/projects",
                                "/api/v1/crews"
                        ).permitAll()

                        // 이메일 보기 API
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/email-views"
                        ).hasAnyRole(
                                "COMPANY",
                                "CREW"
                        )

                        // 어드민 전용 API
                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")

                        // 기업 전용 API
                        .requestMatchers(
                                "/api/v1/landing/company",
                                "/api/v1/companies/**"
                        ).hasRole("COMPANY")

                        // 크루 전용 API
                        .requestMatchers(
                                "/api/v1/landing/crew",
                                "/api/v1/crews/me",
                                "/api/v1/crews/dashboard",
                                "/api/v1/crews/applications",
                                "/api/v1/crews/applications/**",
                                "/api/v1/crews/projects/**"
                        ).hasRole("CREW")

                        // 프로젝트 Q&A 조회
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/projects/*/questions",
                                "/api/v1/projects/*/questions/*"
                        ).authenticated()

                        // 프로젝트 Q&A 작성
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/projects/*/questions"
                        ).hasAnyRole(
                                "CREW",
                                "COMPANY"
                        )

                        // 프로젝트 Q&A 답변
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/projects/*/questions/*/answer"
                        ).hasAnyRole(
                                "COMPANY",
                                "ADMIN"
                        )

                        // 크루 프로젝트 지원/북마크 API
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/projects/*/applications",
                                "/api/v1/projects/*/bookmarks"
                        ).hasRole("CREW")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/projects/*/applications/me",
                                "/api/v1/projects/*/bookmarks"
                        ).hasRole("CREW")

                        // 프로젝트 상세는 로그인만 하면 접근 가능
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/projects/*"
                        ).authenticated()

                        // 크루 대표 프로젝트 전체보기
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/crews/*/projects"
                        ).authenticated()

                        // 크루 상세도 로그인만 하면 접근 가능
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/crews/*"
                        ).authenticated()

                        .anyRequest()
                        .authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .csrf(
                        AbstractHttpConfigurer::disable
                )

                .headers(headers ->
                        headers.frameOptions(frameOptions ->
                                frameOptions.sameOrigin()
                        )
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .exceptionHandling(exception ->
                        exception
                                .accessDeniedHandler(
                                        customAccessDeniedHandler
                                )
                                .authenticationEntryPoint(
                                        jwtAuthenticationEntryPoint
                                )
                );

        return http.build();
    }
}