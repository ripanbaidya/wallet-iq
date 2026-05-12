package online.walletiq.security;

import online.walletiq.config.properties.JwtSecurityProperties;
import online.walletiq.enums.ErrorCode;
import online.walletiq.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final JwtSecurityProperties jwtProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractJwtFromToken(request);
        // No token, continue as anonymous
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // If already authenticated, skip
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            authenticate(jwt, request);
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            log.warn("JWT authentication failed for '{}': {}", request.getRequestURI(),
                e.getMessage());
            authenticationEntryPoint.commence(request, response, new AuthenticationException(e.getMessage(), e) {
            });
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().replace("/api/v1", "");
        return path.startsWith("/auth/")
            || path.startsWith("/test/")
            || path.startsWith("/error")
            || path.startsWith("/actuator")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs");
    }

    /**
     * extracts JWT token from the Authorization header
     *
     * @return actual jwt token string or null if not found
     */
    private String extractJwtFromToken(@NonNull HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.header());
        if (header != null && header.startsWith(jwtProperties.prefix())) {
            return header.substring(jwtProperties.prefix().length());
        }
        return null;
    }

    /**
     * Validates the token, checks it is an access token, then sets authentication in
     * the SecurityContext.
     *
     * @throws JwtAuthenticationException if token is invalid, expired, or is not an
     *                                    access token
     */
    private void authenticate(String jwt, HttpServletRequest request) {
        // Validate the jwt token
        log.debug("Jwt Received: {}", jwt);
        if (!jwtService.isTokenValid(jwt)) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        }

        // Reject refresh tokens used as access tokens
        log.debug("JWT validated successfully");
        if (!jwtService.isAccessToken(jwt)) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID,
                "Refresh tokens cannot be used to authenticate requests");
        }

        // Extract the email and load user
        String email = jwtService.extractEmail(jwt);
        log.debug("JWT email extracted: {}", email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Set authentication in SecurityContext
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}