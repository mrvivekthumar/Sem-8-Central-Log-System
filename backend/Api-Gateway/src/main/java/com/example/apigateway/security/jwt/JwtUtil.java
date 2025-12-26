package com.example.apigateway.security.jwt;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;

/**
 * JwtUtil - JWT Token Validation and Processing Utility
 * 
 * Purpose:
 * - Validates JWT tokens for authentication
 * - Extracts user information from tokens
 * - Verifies token signature and expiration
 * - Provides centralized JWT operations for Gateway
 * 
 * Token Structure:
 * {
 * "sub": "username",
 * "userId": 123,
 * "role": "STUDENT/FACULTY",
 * "iat": 1234567890,
 * "exp": 1234567890
 * }
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${security.jwt.secret}")
    private String SECRET;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("JwtUtil initializing...");

        try {
            // Pre-generate signing key for performance
            this.signingKey = generateSigningKey();
            logger.info("✓ JWT signing key generated successfully");
            logger.info("✓ JWT validation ready");
            logger.info("=================================================");
        } catch (Exception e) {
            logger.error("❌ Failed to initialize JwtUtil: {}", e.getMessage(), e);
            throw new RuntimeException("JWT initialization failed", e);
        }
    }

    /**
     * Validate JWT token signature and expiration
     * Throws exceptions for invalid tokens
     * 
     * @param token JWT token to validate
     * @throws ExpiredJwtException   if token is expired
     * @throws MalformedJwtException if token format is invalid
     * @throws SignatureException    if signature is invalid
     * @throws JwtException          for other JWT-related errors
     */
    public void validateToken(final String token) {
        logger.debug("🔍 Validating JWT token...");

        try {
            // Parse and validate token
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);

            logger.debug("✓ JWT token validation successful");

        } catch (ExpiredJwtException e) {
            logger.warn("❌ JWT token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.warn("❌ Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.warn("❌ Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("❌ JWT validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Unexpected error during JWT validation: {}", e.getMessage(), e);
            throw new JwtException("Token validation failed", e);
        }
    }

    /**
     * Extract user role from JWT token
     * 
     * @param token JWT token
     * @return User role (e.g., "STUDENT", "FACULTY", "ADMIN")
     * @throws JwtException if role extraction fails
     */
    public String extractRole(String token) {
        logger.debug("📋 Extracting role from JWT token");

        try {
            Claims claims = extractAllClaims(token);
            String role = claims.get("role", String.class);

            if (role == null || role.isBlank()) {
                logger.warn("⚠️ Role claim is missing or empty in token");
                throw new JwtException("Role not found in token");
            }

            logger.debug("✓ Extracted role: {}", role);
            return role;

        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            logger.error("❌ Failed to extract role from token: {}", e.getMessage(), e);
            throw new JwtException("Failed to extract role", e);
        }
    }

    /**
     * Extract user ID from JWT token
     * 
     * @param token JWT token
     * @return User ID
     * @throws JwtException if userId extraction fails
     */
    public Long extractUserId(String token) {
        logger.debug("🆔 Extracting userId from JWT token");

        try {
            Claims claims = extractAllClaims(token);

            // Try to get userId as Long
            Object userIdObj = claims.get("userId");

            if (userIdObj == null) {
                logger.warn("⚠️ UserId claim is missing in token");
                throw new JwtException("UserId not found in token");
            }

            Long userId;
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else {
                userId = Long.parseLong(userIdObj.toString());
            }

            logger.debug("✓ Extracted userId: {}", userId);
            return userId;

        } catch (JwtException e) {
            throw e;
        } catch (NumberFormatException e) {
            logger.error("❌ Invalid userId format in token: {}", e.getMessage());
            throw new JwtException("Invalid userId format", e);
        } catch (Exception e) {
            logger.error("❌ Failed to extract userId from token: {}", e.getMessage(), e);
            throw new JwtException("Failed to extract userId", e);
        }
    }

    /**
     * Extract username/subject from JWT token
     * 
     * @param token JWT token
     * @return Username
     * @throws JwtException if subject extraction fails
     */
    public String extractUsername(String token) {
        logger.debug("👤 Extracting username from JWT token");

        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();

            if (username == null || username.isBlank()) {
                logger.warn("⚠️ Subject (username) is missing in token");
                throw new JwtException("Username not found in token");
            }

            logger.debug("✓ Extracted username: {}", username);
            return username;

        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            logger.error("❌ Failed to extract username from token: {}", e.getMessage(), e);
            throw new JwtException("Failed to extract username", e);
        }
    }

    /**
     * Extract all claims from JWT token
     * Private helper method for claim extraction
     * 
     * @param token JWT token
     * @return Claims object containing all token claims
     * @throws JwtException if claims extraction fails
     */
    private Claims extractAllClaims(String token) {
        logger.trace("📦 Extracting all claims from token");

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            logger.trace("✓ Claims extracted successfully");
            return claims;

        } catch (Exception e) {
            logger.error("❌ Failed to extract claims from token: {}", e.getMessage());
            throw new JwtException("Failed to parse token claims", e);
        }
    }

    /**
     * Generate signing key from secret
     * Uses HMAC-SHA algorithm for signing
     * 
     * @return SecretKey for JWT signing/verification
     */
    private SecretKey generateSigningKey() {
        logger.trace("🔑 Generating signing key from secret");

        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            logger.trace("✓ Signing key generated successfully");
            return key;

        } catch (Exception e) {
            logger.error("❌ Failed to generate signing key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT signing key", e);
        }
    }

    /**
     * Check if token is expired
     * Utility method for expiration checking
     * 
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            boolean expired = claims.getExpiration().before(new java.util.Date());
            logger.debug("Token expiration check: {}", expired ? "EXPIRED" : "VALID");
            return expired;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}
