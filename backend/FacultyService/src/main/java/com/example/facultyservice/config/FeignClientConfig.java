package com.example.facultyservice.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Feign Client Configuration for Faculty Service
 * 
 * Purpose:
 * - Configure HTTP client for inter-service communication
 * - Add request interceptors for headers (JWT, Correlation ID)
 * - Set up error handling for failed requests
 * - Enable request/response logging
 * 
 * Used for:
 * - Faculty Service → Student Service communication
 */
@Configuration
public class FeignClientConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeignClientConfig.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Feign Logger Level
     * FULL = Log headers, body, and metadata for both requests and responses
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        logger.info("Configuring Feign logger level: FULL");
        return Logger.Level.FULL;
    }

    /**
     * Request Interceptor
     * Adds necessary headers to all Feign requests
     * - Authorization: JWT token from incoming request
     * - X-Correlation-Id: For request tracing
     * - X-User-Id: Current user ID
     * - X-User-Role: Current user role
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        logger.info("Configuring Feign request interceptor");

        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Forward Authorization header (JWT token)
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && !authHeader.isEmpty()) {
                    requestTemplate.header("Authorization", authHeader);
                    logger.debug("✓ Added Authorization header to Feign request");
                }

                // Forward Correlation ID for request tracking
                String correlationId = request.getHeader("X-Correlation-Id");
                if (correlationId != null && !correlationId.isEmpty()) {
                    requestTemplate.header("X-Correlation-Id", correlationId);
                    logger.debug("✓ Added X-Correlation-Id: {}", correlationId);
                }

                // Forward User ID
                String userId = request.getHeader("X-User-Id");
                if (userId != null && !userId.isEmpty()) {
                    requestTemplate.header("X-User-Id", userId);
                    logger.debug("✓ Added X-User-Id: {}", userId);
                }

                // Forward User Role
                String userRole = request.getHeader("X-User-Role");
                if (userRole != null && !userRole.isEmpty()) {
                    requestTemplate.header("X-User-Role", userRole);
                    logger.debug("✓ Added X-User-Role: {}", userRole);
                }

                // Add Content-Type if not present
                if (!requestTemplate.headers().containsKey("Content-Type")) {
                    requestTemplate.header("Content-Type", "application/json");
                }
            } else {
                logger.warn("⚠️ No request context available for Feign interceptor");
            }
        };
    }

    /**
     * Custom Error Decoder
     * Handles errors from Feign client calls
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        logger.info("Configuring custom Feign error decoder");

        return (methodKey, response) -> {
            logger.error("Feign client error: Method={}, Status={}, Reason={}",
                    methodKey, response.status(), response.reason());

            switch (response.status()) {
                case 400:
                    logger.error("Bad Request: Invalid data sent to service");
                    return new BadRequestException("Invalid request to " + methodKey);
                case 401:
                    logger.error("Unauthorized: Authentication failed");
                    return new UnauthorizedException("Authentication failed for " + methodKey);
                case 403:
                    logger.error("Forbidden: Access denied");
                    return new ForbiddenException("Access denied for " + methodKey);
                case 404:
                    logger.error("Not Found: Resource does not exist");
                    return new NotFoundException("Resource not found for " + methodKey);
                case 500:
                    logger.error("Internal Server Error: Service error");
                    return new InternalServerException("Internal error in " + methodKey);
                case 503:
                    logger.error("Service Unavailable: Service is down");
                    return new ServiceUnavailableException("Service unavailable for " + methodKey);
                default:
                    logger.error("Unexpected error: Status {}", response.status());
                    return new Exception("Generic error for " + methodKey);
            }
        };
    }

    // Custom Exception Classes
    public static class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends Exception {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    public static class ForbiddenException extends Exception {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class InternalServerException extends Exception {
        public InternalServerException(String message) {
            super(message);
        }
    }

    public static class ServiceUnavailableException extends Exception {
        public ServiceUnavailableException(String message) {
            super(message);
        }
    }
}
