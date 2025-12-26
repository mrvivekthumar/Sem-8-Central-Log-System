package com.example.apigateway.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.example.apigateway.security.jwt.JwtUtil;

@Component
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
        logger.info("AuthenticationFilter initialized");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();

            logger.debug("Processing request: {} {}", method, path);

            // Allow CORS preflight
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                logger.debug("CORS preflight request detected for: {}", path);
                return chain.filter(exchange);
            }

            ServerHttpResponse response = exchange.getResponse();

            // If route is public, skip auth
            if (!routeValidator.isSecured.test(exchange.getRequest())) {
                logger.debug("Public route detected, skipping authentication: {}", path);
                return chain.filter(exchange);
            }
            logger.info("Secured route detected, validating authentication: {} {}", method, path);

            // Authorization header check
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header for: {} {}", method, path);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String token = authHeader.substring(7);
            logger.debug("Token extracted from Authorization header for: {}", path);

            try {
                // 1. Validate token signature & expiry
                jwtUtil.validateToken(token);
                logger.debug("Token validation successful for: {}", path);

                // 2. Extract role from token
                String role = jwtUtil.extractRole(token);
                logger.debug("Extracted role '{}' from token for: {}", role, path);

                // 3. Get allowed roles for this route
                List<String> allowedRoles = routeValidator.getAllowedRoles(exchange.getRequest());
                logger.debug("Allowed roles for {}: {}", path, allowedRoles);

                // 4. Enforce role-based authorization
                if (!allowedRoles.isEmpty() && !allowedRoles.contains(role)) {
                    logger.warn("Access denied. Role '{}' not allowed for path: {}. Allowed roles: {}",
                            role, path, allowedRoles);
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    return response.setComplete();
                }

                logger.info("Authentication and authorization successful for: {} {} with role: {}",
                        method, path, role);

            } catch (Exception ex) {
                logger.error("Authentication/Authorization failed for {} {}: {}",
                        method, path, ex.getMessage(), ex);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // future extensibility
    }
}
