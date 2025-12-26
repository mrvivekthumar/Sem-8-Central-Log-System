package com.example.apigateway.security;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RouteValidator {

        private static final Logger logger = LoggerFactory.getLogger(RouteValidator.class);

        private static final List<String> OPEN_API_ENDPOINTS = List.of(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/token",
                        "/api/auth/validate",
                        "/auth/register",
                        "/auth/login",
                        "/auth/token",
                        "/auth/validate",
                        "/eureka");

        public static final Map<String, List<String>> ROLE_BASED_ROUTES = Map.of(
                        "/api/student", List.of("STUDENT"),
                        "/api/faculty", List.of("FACULTY"),
                        "/student", List.of("STUDENT"),
                        "/faculty", List.of("FACULTY"));

        @PostConstruct
        public void init() {
                logger.info("RouteValidator initialized");
                logger.info("Open API Endpoints configured: {}", OPEN_API_ENDPOINTS);
                logger.info("Role-based routes configured: {}", ROLE_BASED_ROUTES);
        }

        public List<String> getAllowedRoles(ServerHttpRequest request) {
                String path = request.getURI().getPath();
                logger.debug("Getting allowed roles for path: {}", path);

                List<String> roles = ROLE_BASED_ROUTES.entrySet().stream()
                                .filter(entry -> path.startsWith(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse(List.of());

                logger.debug("Allowed roles for path '{}': {}", path, roles);
                return roles;
        }

        public Predicate<ServerHttpRequest> isSecured = request -> {
                String path = request.getURI().getPath();
                boolean isSecured = OPEN_API_ENDPOINTS.stream()
                                .noneMatch(path::startsWith);

                logger.debug("Route '{}' is {}secured", path, isSecured ? "" : "NOT ");
                return isSecured;
        };
}
