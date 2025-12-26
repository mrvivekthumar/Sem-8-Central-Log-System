package com.example.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

        private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

        @PostConstruct
        public void init() {
                logger.info("CorsConfig initialized - CORS configuration will be applied");
        }

        @Bean
        public CorsWebFilter corsWebFilter() {
                logger.info("Configuring CORS Web Filter for API Gateway");

                CorsConfiguration corsConfig = new CorsConfiguration();

                // Allow all origins for development (change in production)
                corsConfig.addAllowedOriginPattern("*");
                logger.debug("CORS: Added allowed origin pattern: *");

                // Specific allowed origins
                List<String> allowedOrigins = Arrays.asList(
                                "https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app",
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://localhost:5174");
                corsConfig.setAllowedOrigins(allowedOrigins);
                logger.info("CORS: Allowed origins configured: {}", allowedOrigins);

                // Allow all HTTP methods
                List<String> allowedMethods = Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH");
                corsConfig.setAllowedMethods(allowedMethods);
                logger.info("CORS: Allowed methods configured: {}", allowedMethods);

                // Allow all headers
                corsConfig.setAllowedHeaders(List.of("*"));
                logger.debug("CORS: All headers allowed");

                // Allow credentials (cookies, authorization headers)
                corsConfig.setAllowCredentials(true);
                logger.debug("CORS: Credentials allowed");

                // Expose headers that frontend can read
                List<String> exposedHeaders = Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "X-Correlation-Id",
                                "Access-Control-Allow-Origin",
                                "Access-Control-Allow-Credentials");
                corsConfig.setExposedHeaders(exposedHeaders);
                logger.info("CORS: Exposed headers configured: {}", exposedHeaders);

                // Cache preflight response for 1 hour
                corsConfig.setMaxAge(3600L);
                logger.debug("CORS: Max age set to 3600 seconds (1 hour)");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfig);

                logger.info("CORS Web Filter configured successfully for all paths (/**)");
                return new CorsWebFilter(source);
        }
}
