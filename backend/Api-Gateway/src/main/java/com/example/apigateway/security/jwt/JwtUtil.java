package com.example.apigateway.security.jwt;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${security.jwt.secret}")
    private String SECRET;

    @PostConstruct
    public void init() {
        logger.info("JwtUtil initialized and ready to validate tokens");
    }

    public void validateToken(final String token) {
        logger.debug("Validating JWT token");
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            logger.debug("JWT token validation successful");
        } catch (JwtException e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT token validation: {}", e.getMessage(), e);
            throw e;
        }
    }

    private SecretKey getSignKey() {
        logger.trace("Generating signing key from secret");
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractRole(String token) {
        logger.debug("Extracting role from token");
        try {
            Claims claims = extractAllClaims(token);
            String role = claims.get("role", String.class);
            logger.debug("Extracted role: {}", role);
            return role;
        } catch (Exception e) {
            logger.error("Failed to extract role from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Long extractUserId(String token) {
        logger.debug("Extracting userId from token");
        try {
            Claims claims = extractAllClaims(token);
            Long userId = claims.get("userId", Long.class);
            logger.debug("Extracted userId: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Failed to extract userId from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        logger.trace("Extracting all claims from token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            logger.trace("Claims extracted successfully");
            return claims;
        } catch (Exception e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }
}
