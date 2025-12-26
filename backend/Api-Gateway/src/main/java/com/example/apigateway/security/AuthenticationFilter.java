package com.example.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.apigateway.security.jwt.JwtUtil;

import reactor.core.publisher.Mono;

/**
 * AuthenticationFilter - Gateway filter for JWT-based authentication
 * 
 * Purpose:
 * - Validates JWT tokens for secured routes
 * - Extracts user information from JWT
 * - Enforces role-based access control
 * - Forwards authenticated requests to downstream services
 * 
 * Flow:
 * 1. Check if route is public (skip authentication)
 * 2. Extract JWT token from Authorization header
 * 3. Validate token signature and expiration
 * 4. Extract user role from token
 * 5. Verify role has access to requested route
 * 6. Add user info to request headers for downstream services
 * 7. Allow or deny request based on validation
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
        logger.info("=================================================");
        logger.info("AuthenticationFilter initialized successfully");
        logger.info("JWT-based authentication enabled for secured routes");
        logger.info("=================================================");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String path = request.getURI().getPath();
            String method = request.getMethod().name();
            String correlationId = request.getHeaders().getFirst("X-Correlation-Id");

            logger.debug("🔍 AuthenticationFilter processing: {} {} [Correlation-ID: {}]",
                    method, path, correlationId);

            // =========================================
            // STEP 1: Handle CORS Preflight Requests
            // =========================================
            if (request.getMethod() == HttpMethod.OPTIONS) {
                logger.debug("✓ CORS preflight request detected for: {} - Allowing without auth", path);
                return chain.filter(exchange);
            }

            // =========================================
            // STEP 2: Check if Route is Public
            // =========================================
            if (!routeValidator.isSecured.test(request)) {
                logger.debug("✓ Public route detected: {} {} - Skipping authentication", method, path);
                return chain.filter(exchange);
            }

            logger.debug("🔒 Secured route detected: {} {} - Authentication required", method, path);

            // =========================================
            // STEP 3: Extract Authorization Header
            // =========================================
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || authHeader.isBlank()) {
                logger.warn("❌ Missing Authorization header for: {} {} [Correlation-ID: {}]",
                        method, path, correlationId);
                return handleUnauthorized(response, "Missing Authorization header");
            }

            if (!authHeader.startsWith("Bearer ")) {
                logger.warn("❌ Invalid Authorization header format for: {} {} [Correlation-ID: {}]",
                        method, path, correlationId);
                return handleUnauthorized(response, "Invalid Authorization header format. Expected: Bearer <token>");
            }

            // =========================================
            // STEP 4: Extract JWT Token
            // =========================================
            String token = authHeader.substring(7);
            logger.debug("✓ JWT token extracted from Authorization header");

            try {
                // =========================================
                // STEP 5: Validate Token
                // =========================================
                jwtUtil.validateToken(token);
                logger.debug("✓ JWT token validation successful");

                // =========================================
                // STEP 6: Extract User Information
                // =========================================
                String userRole = jwtUtil.extractRole(token);
                Long userId = jwtUtil.extractUserId(token);

                logger.debug("✓ Extracted user info - UserID: {}, Role: {}", userId, userRole);

                // =========================================
                // STEP 7: Role-Based Authorization
                // =========================================
                if (!routeValidator.hasRoleAccess(request, userRole)) {
                    logger.warn("❌ Access denied for role '{}' to path: {} {} [Correlation-ID: {}]",
                            userRole, method, path, correlationId);
                    return handleForbidden(response,
                            String.format("Access denied. Role '%s' not allowed for this resource", userRole));
                }

                logger.info(
                        "✅ Authentication & Authorization successful: {} {} | UserID: {} | Role: {} [Correlation-ID: {}]",
                        method, path, userId, userRole, correlationId);

                // =========================================
                // STEP 8: Add User Info to Request Headers
                // =========================================
                // Forward user information to downstream services
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(builder -> {
                            builder.header("X-User-Id", String.valueOf(userId));
                            builder.header("X-User-Role", userRole);
                            builder.header("X-Auth-Token", token);
                        })
                        .build();

                logger.debug("✓ User info added to request headers for downstream services");

                // =========================================
                // STEP 9: Forward to Downstream Service
                // =========================================
                return chain.filter(modifiedExchange);

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("❌ JWT token expired for: {} {} [Correlation-ID: {}]",
                        method, path, correlationId);
                return handleUnauthorized(response, "Token expired. Please login again");

            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.warn("❌ Malformed JWT token for: {} {} [Correlation-ID: {}]",
                        method, path, correlationId);
                return handleUnauthorized(response, "Invalid token format");

            } catch (io.jsonwebtoken.security.SignatureException e) {
                logger.warn("❌ Invalid JWT signature for: {} {} [Correlation-ID: {}]",
                        method, path, correlationId);
                return handleUnauthorized(response, "Invalid token signature");

            } catch (Exception e) {
                logger.error("❌ Authentication failed for: {} {} [Correlation-ID: {}] - Error: {}",
                        method, path, correlationId, e.getMessage(), e);
                return handleUnauthorized(response, "Authentication failed: " + e.getMessage());
            }
        };
    }

    /**
     * Handle unauthorized access (401)
     * Returns appropriate error response to client
     */
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String errorBody = String.format(
                "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"status\":401}",
                message);

        logger.debug("Sending 401 Unauthorized response: {}", message);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    /**
     * Handle forbidden access (403)
     * Returns appropriate error response to client
     */
    private Mono<Void> handleForbidden(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");

        String errorBody = String.format(
                "{\"error\":\"Forbidden\",\"message\":\"%s\",\"status\":403}",
                message);

        logger.debug("Sending 403 Forbidden response: {}", message);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    /**
     * Configuration class for filter
     * Can be extended with additional configuration options
     */
    public static class Config {
        // Configuration properties can be added here
        // Example: private boolean strictMode;
    }
}
