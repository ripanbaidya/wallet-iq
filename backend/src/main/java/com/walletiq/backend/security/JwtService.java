package com.walletiq.backend.security;

import com.walletiq.backend.config.properties.JwtSecurityProperties;
import com.walletiq.backend.config.properties.RSAProperties;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.JwtAuthenticationException;
import com.walletiq.backend.util.KeyUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtSecurityProperties jwtProperties;
    private final RSAProperties rsaProperties;
    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private JwtParser jwtParser;

    // Initialization

    /**
     * Loads RSA keys and builds the JWT parser.
     */
    @PostConstruct
    private void init() {
        log.info("Initializing RSA key pair for Jwt authentication...");

        String privateKeyPath = rsaProperties.privateKeyPath();
        String publicKeyPath = rsaProperties.publicKeyPath();

        if (StringUtils.isBlank(privateKeyPath) || StringUtils.isBlank(publicKeyPath)) {
            throw new IllegalStateException("RSA key paths must be configured");
        }

        // KeyLoadException (RuntimeException) propagates as-is if keys fail to load
        this.privateKey = KeyUtils.loadPrivateKey(privateKeyPath, resourceLoader);
        this.publicKey = KeyUtils.loadPublicKey(publicKeyPath, resourceLoader);

        this.jwtParser = Jwts.parser()
            .verifyWith(publicKey)
            .requireIssuer(jwtProperties.issuer())
            .clockSkewSeconds(60)
            .build();

        log.info("RSA key pair initialized successfully");
    }

    // Token Generation

    public String generateAccessToken(String userId, String email) {
        Map<String, Object> claims = Map.of(
            CLAIM_USER_ID, userId,
            CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS
        );
        return buildToken(claims, email, getExpirationSeconds(TOKEN_TYPE_ACCESS));
    }

    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = Map.of(
            CLAIM_USER_ID, userId,
            CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH
        );
        return buildToken(claims, email, getExpirationSeconds(TOKEN_TYPE_REFRESH));
    }

    // Token Validation

    /**
     * Returns true only if the token parses and passes all JJWT validations.
     * Does not throw — all outcomes are logged and collapsed to boolean.
     */
    public boolean isTokenValid(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        try {
            jwtParser.parseSignedClaims(stripBearerPrefix(token));
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT rejected — token expired: {}", ex.getMessage());
            return false;
        } catch (SignatureException ex) {
            log.warn("JWT rejected — signature invalid: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException | UnsupportedJwtException ex) {
            log.warn("JWT rejected — malformed or unsupported: {}", ex.getMessage());
            return false;
        } catch (JwtException ex) {
            log.warn("JWT rejected: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Returns true if the token's expiration is in the past.
     * Returns true for unparseable tokens — treat them as expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (JwtAuthenticationException ex) {
            // Malformed or invalid tokens are treated as expired
            return true;
        }
    }

    // Claim Extraction

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractClaims(token));
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
    }

    public String extractUserId(String token) {
        String userId = extractClaims(token).get(CLAIM_USER_ID, String.class);
        if (StringUtils.isBlank(userId)) {
            log.warn("JWT is valid but missing required claim: {}", CLAIM_USER_ID);
            throw new JwtAuthenticationException(
                ErrorCode.TOKEN_MISSING_CLAIM,
                "Token is missing required claim: " + CLAIM_USER_ID
            );
        }
        return userId;
    }

    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
    }

    // Private Helpers

    private Claims extractClaims(String token) {
        try {
            return jwtParser
                .parseSignedClaims(stripBearerPrefix(token))
                .getPayload();
        } catch (ExpiredJwtException ex) {
            log.debug("JWT expired: {}", ex.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (MalformedJwtException | UnsupportedJwtException ex) {
            log.warn("JWT malformed or unsupported: {}", ex.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_UNSUPPORTED);
        } catch (JwtException ex) {
            log.warn("JWT parsing failed: {}", ex.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        }
    }

    private String buildToken(Map<String, Object> claims, String email, long expirationSeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationSeconds);
        return Jwts.builder()
            .claims(claims)
            .issuer(jwtProperties.issuer())
            .subject(email)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(privateKey, Jwts.SIG.RS512)
            .compact();
    }

    /**
     * Get the expiration time in seconds for the given token type.
     *
     * @param type the token type
     * @return expiration time in seconds
     */
    private long getExpirationSeconds(String type) {
        return switch (type) {
            case TOKEN_TYPE_ACCESS -> Duration.ofMillis(jwtProperties.accessToken().expiry()).getSeconds();
            case TOKEN_TYPE_REFRESH -> Duration.ofMillis(jwtProperties.refreshToken().expiry()).getSeconds();
            default -> throw new IllegalArgumentException("Unknown token type: " + type);
        };
    }

    /**
     * Stripe the bearer prefix from the token if present.
     *
     * @param token the token string
     * @return stripped token string
     */
    private String stripBearerPrefix(String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length()).trim();
        }
        return token;
    }
}