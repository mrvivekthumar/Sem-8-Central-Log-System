package com.example.apigateway.security;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * RouteValidator - Validates and manages route access control
 * Determines which routes are public vs secured
 * Maps routes to required roles for authorization
 */
@Component
public class RouteValidator {

        private static final Logger logger = LoggerFactory.getLogger(RouteValidator.class);

        /**
         * Public endpoints that don't require authentication
         * These routes bypass JWT validation
         */
        private static final List<String> PUBLIC_ENDPOINTS = List.of(
                        // Authentication endpoints
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/token",
                        "/api/auth/validate",

                        // Direct service paths (if gateway forwards)
                        "/auth/register",
                        "/auth/login",
                        "/auth/token",
                        "/auth/validate",

                        // Health check and monitoring
                        "/actuator/health",
                        "/actuator/info",
                        "/actuator/prometheus");

        /**
         * Role-based route access control
         * Maps route patterns to allowed roles
         * Key: Route prefix
         * Value: List of roles that can access this route
         */
        private static final Map<String, List<String>> ROLE_BASED_ROUTES = Map.of(
                        // Faculty routes - Only FACULTY role
                        "/api/faculty", List.of("FACULTY"),
                        "/faculty", List.of("FACULTY"),

                        // Student routes - Only STUDENT role
                        "/api/student", List.of("STUDENT"),
                        "/student", List.of("STUDENT"),

                        // Admin routes (if you add admin functionality later)
                        "/api/admin", List.of("ADMIN"),
                        "/admin", List.of("ADMIN"));

        @PostConstruct
        public void init() {
                logger.info("=================================================");
                logger.info("RouteValidator initialized successfully");
                logger.info("=================================================");
                logger.info("Public Endpoints (No Auth Required):");
                PUBLIC_ENDPOINTS.forEach(endpoint -> logger.info("  ✓ {}", endpoint));
                logger.info("=================================================");
                logger.info("Role-Based Access Control:");
                ROLE_BASED_ROUTES.forEach((route, roles) -> logger.info("  ✓ {} → Allowed Roles: {}", route, roles));
                logger.info("=================================================");
        }

        /**
         * Get allowed roles for a specific request path
         * Used for authorization after authentication
         * 
         * @param request The incoming HTTP request
         * @return List of roles allowed to access this path
         */
        public List<String> getAllowedRoles(ServerHttpRequest request) {
                String path = request.getURI().getPath();
                logger.debug("Checking allowed roles for path: {}", path);

                // Find matching route pattern and return allowed roles
                List<String> allowedRoles = ROLE_BASED_ROUTES.entrySet().stream()
                                .filter(entry -> path.startsWith(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse(List.of()); // Empty list = no role restriction

                if (allowedRoles.isEmpty()) {
                        logger.debug("No specific role restrictions for path: {}", path);
                } else {
                        logger.debug("Path '{}' requires one of these roles: {}", path, allowedRoles);
                }

                return allowedRoles;
        }

        /**
         * Predicate to check if a route is secured (requires authentication)
         * Returns true if route needs authentication, false if public
         */
        public Predicate<ServerHttpRequest> isSecured = request -> {
                String path = request.getURI().getPath();
                String method = request.getMethod().name();

                // Check if path matches any public endpoint
                boolean isPublic = PUBLIC_ENDPOINTS.stream()
                                .anyMatch(publicPath -> path.startsWith(publicPath) || path.equals(publicPath));

                boolean isSecured = !isPublic;

                if (isSecured) {
                        logger.debug("🔒 Secured route detected: {} {}", method, path);
                } else {
                        logger.debug("🔓 Public route detected: {} {}", method, path);
                }

                return isSecured;
        };

        /**
         * Check if a specific role has access to a path
         * Useful for fine-grained authorization checks
         * 
         * @param request  The incoming HTTP request
         * @param userRole The role of the authenticated user
         * @return true if role has access, false otherwise
         */
        public boolean hasRoleAccess(ServerHttpRequest request, String userRole) {
                List<String> allowedRoles = getAllowedRoles(request);

                // If no role restrictions, allow all authenticated users
                if (allowedRoles.isEmpty()) {
                        logger.debug("No role restrictions - access granted for role: {}", userRole);
                        return true;
                }

                // Check if user's role is in allowed roles list
                boolean hasAccess = allowedRoles.contains(userRole);

                if (hasAccess) {
                        logger.debug("✓ Role '{}' has access to: {}", userRole, request.getURI().getPath());
                } else {
                        logger.warn("✗ Role '{}' denied access to: {}. Required roles: {}",
                                        userRole, request.getURI().getPath(), allowedRoles);
                }

                return hasAccess;
        }

        /**
         * Check if path is a public endpoint
         * 
         * @param path The request path
         * @return true if public, false if secured
         */
        public boolean isPublicEndpoint(String path) {
                return PUBLIC_ENDPOINTS.stream()
                                .anyMatch(publicPath -> path.startsWith(publicPath) || path.equals(publicPath));
        }
}
