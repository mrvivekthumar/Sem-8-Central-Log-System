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
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // Allow CORS preflight
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            ServerHttpResponse response = exchange.getResponse();

            // If route is public, skip auth
            if (!routeValidator.isSecured.test(exchange.getRequest())) {
                return chain.filter(exchange);
            }

            // Authorization header check
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String token = authHeader.substring(7);

            try {
                // 1. Validate token signature & expiry
                jwtUtil.validateToken(token);

                // 2. Extract role from token
                String role = jwtUtil.extractRole(token);

                // 3. Get allowed roles for this route
                List<String> allowedRoles = routeValidator.getAllowedRoles(exchange.getRequest());

                // 4. Enforce role-based authorization
                if (!allowedRoles.isEmpty() && !allowedRoles.contains(role)) {
                    logger.warn(
                            "Access denied. Role '{}' not allowed for path {}",
                            role,
                            exchange.getRequest().getURI().getPath());
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    return response.setComplete();
                }

            } catch (Exception ex) {
                logger.error("Authentication/Authorization failed: {}", ex.getMessage());
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
