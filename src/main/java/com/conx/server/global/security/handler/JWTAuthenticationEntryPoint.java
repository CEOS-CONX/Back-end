package com.conx.server.global.security.handler;

import com.conx.server.global.exception.CustomAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException ae) throws IOException, ServletException {
        if (ae instanceof CustomAuthenticationException cae){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.create(cae.getCode());

            String responseBody = objectMapper.writeValueAsString(errorResponseDTO);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(cae.getCode().getStatus().value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseBody);
        }
    }
}
