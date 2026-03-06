package com.walletiq.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.payload.response.error.ErrorDetail;
import com.walletiq.backend.payload.response.error.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthenticated access attempts (HTTP 401 Unauthorized).
 * Invoked when no JWT is present, the token is expired, malformed, or fails
 * signature verification.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("Unauthorized access attempt to {} from {} - Reason: {}", request.getRequestURI(),
            request.getRemoteAddr(), authException.getMessage());

        var errorResponse = ErrorResponse.of(ErrorDetail.builder()
            .code(ErrorCode.UNAUTHENTICATED)
            .path(request.getRequestURI())
            .build());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
