package com.example.authenticationservice.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.authenticationservice.domain.UserCredential;
import com.example.authenticationservice.exception.AuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${security.jwt.secret}")
    private String secret;

    private static final long TOKEN_VALIDITY = 1000 * 60 * 30; // 30 minutes

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        logger.info("JwtService initialized with token validity: {} minutes", TOKEN_VALIDITY / 1000 / 60);
    }

    public void validateToken(String token) {
        logger.debug("Validating JWT token");
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            logger.debug("JWT token validation successful");
        } catch (JwtException e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            throw new AuthenticationException("Invalid or expired token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT token validation failed - illegal argument: {}", e.getMessage());
            throw new AuthenticationException("Invalid token format");
        }
    }

    public String extractUsername(String token) {
        logger.debug("Extracting username from JWT token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = claims.getSubject();
            logger.debug("Extracted username: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String generateToken(UserCredential user) {
        logger.info("Generating JWT token for user: {}, ID: {}, Role: {}",
                user.getEmail(), user.getId(), user.getRole());
        try {
            String token = Jwts.builder()
                    .subject(user.getEmail())
                    .claim("userId", user.getId())
                    .claim("role", user.getRole())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                    .signWith(getSignKey())
                    .compact();

            logger.info("JWT token generated successfully for user: {}", user.getEmail());
            logger.debug("Token preview: {}...", token.substring(0, Math.min(30, token.length())));
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate token for user {}: {}", user.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    public Long extractUserId(String token) {
        logger.debug("Extracting userId from JWT token");
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

    public String extractRole(String token) {
        logger.debug("Extracting role from JWT token");
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

    private Claims extractAllClaims(String token) {
        logger.trace("Extracting all claims from JWT token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            logger.trace("All claims extracted successfully");
            return claims;
        } catch (Exception e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    private SecretKey getSignKey() {
        return signingKey;
    }
}
