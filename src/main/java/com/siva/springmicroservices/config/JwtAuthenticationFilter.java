package com.siva.springmicroservices.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.springmicroservices.dto.ErrorResponse;
import com.siva.springmicroservices.security.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        String jwt =
                authHeader.substring(7);

        try {

            String username =
                    jwtService.extractUsername(jwt);

            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username);

                if (jwtService.isTokenValid(
                        jwt,
                        userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }
            }

            filterChain.doFilter(
                    request,
                    response);

        }
        catch (ExpiredJwtException ex) {

            writeErrorResponse(
                    response,
                    "Token has expired");

            return;
        }
        catch (MalformedJwtException ex) {

            writeErrorResponse(
                    response,
                    "Malformed JWT token");

            return;
        }
        catch (SignatureException ex) {

            writeErrorResponse(
                    response,
                    "Invalid JWT signature");

            return;
        }


    }

    private void writeErrorResponse(
            HttpServletResponse response,
            String message) throws IOException {

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .message(message)
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
