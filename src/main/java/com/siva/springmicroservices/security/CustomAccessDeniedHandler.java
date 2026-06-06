package com.siva.springmicroservices.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.springmicroservices.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(accessDeniedException.getMessage())
                        .status(HttpServletResponse.SC_FORBIDDEN)
                        .timestamp(LocalDateTime.now())
                        .build();

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN);

        response.setContentType("application/json");

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse);
    }
}
