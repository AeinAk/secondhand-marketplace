package com.marketplace.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for JSON Web Token (JWT) generation, parsing, and validation.
 * <p>
 * This component is responsible for creating JWT tokens for authenticated users,
 * validating incoming tokens, and extracting claims such as username, user ID,
 * and role. It uses a secret key configured in the application properties.
 * </p>
 * <p>
 * The token includes the username as the subject, along with custom claims
 * for user ID and role. The expiration time is configurable via application
 * properties, providing flexibility for security policies.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtUtil {

    /**
     * The cryptographic key used for signing and verifying JWT tokens.
     */
    private final SecretKey secretKey;

    /**
     * The expiration time for JWT tokens in milliseconds.
     */
    private final long expirationMs;

    /**
     * Constructs a JwtUtil with the provided secret and expiration time.
     * <p>
     * The secret string is converted to a {@link SecretKey} using the HMAC-SHA
     * algorithm, and the expiration time is stored for token generation.
     * </p>
     *
     * @param secret        the secret key string (must be sufficiently strong)
     * @param expirationMs  the token expiration time in milliseconds
     */
    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a new JWT token for the given user.
     * <p>
     * The token contains the username as the subject, along with custom claims
     * for user ID and role. It is signed with the configured secret key and
     * includes issuance and expiration timestamps.
     * </p>
     *
     * @param username the username of the authenticated user
     * @param userId   the unique identifier of the user
     * @param role     the role of the user (e.g., "USER" or "ADMIN")
     * @return a signed JWT token string
     */
    public String generateToken(String username, Long userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Parses a JWT token and returns its claims payload.
     * <p>
     * This method validates the token signature and extracts the claims.
     * If the token is invalid (e.g., malformed, expired, or tampered with),
     * an exception is thrown during parsing.
     * </p>
     *
     * @param token the JWT token string
     * @return the {@link Claims} object containing all claims
     * @throws io.jsonwebtoken.JwtException if the token is invalid or cannot be parsed
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates a JWT token by checking its signature and expiration.
     * <p>
     * This method attempts to parse the token. If parsing succeeds and the
     * expiration date is after the current time, the token is considered valid.
     * Any exception during parsing (including expired tokens) results in false.
     * </p>
     *
     * @param token the JWT token string
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token the JWT token string
     * @return the username stored in the token's subject claim
     * @throws io.jsonwebtoken.JwtException if the token is invalid
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT token string
     * @return the user ID stored in the "userId" claim
     * @throws io.jsonwebtoken.JwtException if the token is invalid
     */
    public Long extractUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * Extracts the role from a JWT token.
     *
     * @param token the JWT token string
     * @return the role stored in the "role" claim (e.g., "USER" or "ADMIN")
     * @throws io.jsonwebtoken.JwtException if the token is invalid
     */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}