<<<<<<<< HEAD:src/main/java/com/conx/server/global/config/SecurityConfig.java
package com.conx.server.global.config;
========
package com.conx.server.global.security;
>>>>>>>> dev:src/main/java/com/conx/server/global/security/SecurityConfig.java

import com.conx.server.global.security.filter.JWTAuthenticationFilter;
import com.conx.server.global.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

<<<<<<<< HEAD:src/main/java/com/conx/server/global/config/SecurityConfig.java
    public JWTAuthenticationFilter jwtAuthenticationFilter(TokenProvider tokenProvider){
        return new JWTAuthenticationFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            TokenProvider tokenProvider) throws Exception {

========
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
>>>>>>>> dev:src/main/java/com/conx/server/global/security/SecurityConfig.java
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/health/**"
                        ).permitAll()
                        .requestMatchers(
                                "/",
<<<<<<<< HEAD:src/main/java/com/conx/server/global/config/SecurityConfig.java
                                "/css/**", "/images/**", "/favicon.ico/**",
                                "/api/v1/auth/**",
                                "/api/v1/login/**",
                                "/api/v1/email/**"
========
                                "/css/**",
                                "/images/**",
                                "/favicon.ico"
>>>>>>>> dev:src/main/java/com/conx/server/global/security/SecurityConfig.java
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .csrf(AbstractHttpConfigurer::disable)

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .addFilterBefore(
                        jwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}