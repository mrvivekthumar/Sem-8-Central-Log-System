package com.example.apigateway.security;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

        private static final List<String> OPEN_API_ENDPOINTS = List.of(
                        "/auth/register",
                        "/auth/token",
                        "/auth/validate",
                        "/eureka");

        public static final Map<String, List<String>> ROLE_BASED_ROUTES = Map.of(
                        "/student", List.of("STUDENT"),
                        "/faculty", List.of("FACULTY"));

        public List<String> getAllowedRoles(ServerHttpRequest request) {
                return ROLE_BASED_ROUTES.entrySet().stream()
                                .filter(entry -> request.getURI().getPath().startsWith(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse(List.of());
        }

        public Predicate<ServerHttpRequest> isSecured = request -> {
                String path = request.getURI().getPath();
                return OPEN_API_ENDPOINTS.stream()
                                .noneMatch(path::startsWith);
        };
}