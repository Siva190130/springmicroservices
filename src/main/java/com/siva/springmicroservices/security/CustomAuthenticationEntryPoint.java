package com.siva.springmicroservices.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.springmicroservices.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message("Authentication is required")
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                        .timestamp(LocalDateTime.now())
                        .build();

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType("application/json");

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse);
    }
}
