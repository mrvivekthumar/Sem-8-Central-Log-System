package com.example.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for API Gateway
 * Handles Cross-Origin Resource Sharing for frontend applications
 * Configured for both development and production environments
 */
@Configuration
public class CorsConfig {

        private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

        @Value("${spring.profiles.active:default}")
        private String activeProfile;

        @PostConstruct
        public void init() {
                logger.info("=================================================");
                logger.info("CORS Configuration Initializing...");
                logger.info("Active Profile: {}", activeProfile);
                logger.info("=================================================");
        }

        /**
         * Configure CORS Web Filter for reactive Spring Gateway
         * This filter applies CORS configuration to all incoming requests
         * 
         * @return Configured CorsWebFilter bean
         */
        @Bean
        public CorsWebFilter corsWebFilter() {
                logger.info("Configuring CORS Web Filter for API Gateway");

                CorsConfiguration corsConfig = new CorsConfiguration();

                // =========================================
                // ALLOWED ORIGINS CONFIGURATION
                // =========================================

                // Allow origin patterns for flexible matching
                // Use patterns for dynamic subdomains
                List<String> allowedOriginPatterns = Arrays.asList(
                                "https://*.vercel.app", // All Vercel deployments
                                "http://localhost:*", // All localhost ports
                                "http://127.0.0.1:*" // All 127.0.0.1 ports
                );
                corsConfig.setAllowedOriginPatterns(allowedOriginPatterns);
                logger.info("✓ CORS: Allowed origin patterns: {}", allowedOriginPatterns);

                // Specific allowed origins (exact match)
                List<String> allowedOrigins = Arrays.asList(
                                "http://localhost:3000", // React default
                                "http://localhost:5173", // Vite default
                                "http://localhost:5174", // Vite alternate
                                "http://localhost:8080", // Spring Boot default
                                "https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app");
                corsConfig.setAllowedOrigins(allowedOrigins);
                logger.info("✓ CORS: Specific allowed origins: {}", allowedOrigins);

                // =========================================
                // ALLOWED HTTP METHODS
                // =========================================
                List<String> allowedMethods = Arrays.asList(
                                "GET", // Read operations
                                "POST", // Create operations
                                "PUT", // Update operations (full replace)
                                "PATCH", // Update operations (partial)
                                "DELETE", // Delete operations
                                "OPTIONS", // Preflight requests
                                "HEAD" // Metadata requests
                );
                corsConfig.setAllowedMethods(allowedMethods);
                logger.info("✓ CORS: Allowed HTTP methods: {}", allowedMethods);

                // =========================================
                // ALLOWED HEADERS
                // =========================================
                // Allow all headers from client
                corsConfig.setAllowedHeaders(List.of("*"));
                logger.info("✓ CORS: All request headers allowed");

                // =========================================
                // CREDENTIALS SUPPORT
                // =========================================
                // Enable credentials (cookies, authorization headers, TLS certificates)
                corsConfig.setAllowCredentials(true);
                logger.info("✓ CORS: Credentials (cookies, auth headers) allowed");

                // =========================================
                // EXPOSED HEADERS
                // =========================================
                // Headers that frontend JavaScript can access
                List<String> exposedHeaders = Arrays.asList(
                                "Authorization", // JWT tokens
                                "Content-Type", // Response content type
                                "X-Correlation-Id", // Request tracking
                                "X-Request-Id", // Request identification
                                "Access-Control-Allow-Origin", // CORS origin
                                "Access-Control-Allow-Credentials", // CORS credentials
                                "X-Total-Count", // Pagination total
                                "X-Page-Number", // Pagination page
                                "X-Page-Size" // Pagination size
                );
                corsConfig.setExposedHeaders(exposedHeaders);
                logger.info("✓ CORS: Exposed headers to frontend: {}", exposedHeaders);

                // =========================================
                // PREFLIGHT CACHE DURATION
                // =========================================
                // Cache preflight response for 1 hour (3600 seconds)
                // Reduces preflight requests frequency
                corsConfig.setMaxAge(3600L);
                logger.info("✓ CORS: Preflight cache duration: 3600 seconds (1 hour)");

                // =========================================
                // APPLY CONFIGURATION TO ALL ROUTES
                // =========================================
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfig);
                logger.info("✓ CORS: Configuration applied to all routes (/**)");

                logger.info("=================================================");
                logger.info("CORS Web Filter configured successfully!");
                logger.info("=================================================");

                return new CorsWebFilter(source);
        }
}
