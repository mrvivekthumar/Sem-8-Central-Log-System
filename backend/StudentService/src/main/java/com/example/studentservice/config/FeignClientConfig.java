package com.example.studentservice.config;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(FeignClientConfig.class);

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && !authHeader.isEmpty()) {
                    requestTemplate.header("Authorization", authHeader);
                }

                String correlationId = request.getHeader("X-Correlation-Id");
                if (correlationId != null && !correlationId.isEmpty()) {
                    requestTemplate.header("X-Correlation-Id", correlationId);
                }

                String userId = request.getHeader("X-User-Id");
                if (userId != null && !userId.isEmpty()) {
                    requestTemplate.header("X-User-Id", userId);
                }

                String userRole = request.getHeader("X-User-Role");
                if (userRole != null && !userRole.isEmpty()) {
                    requestTemplate.header("X-User-Role", userRole);
                }

                if (!requestTemplate.headers().containsKey("Content-Type")) {
                    requestTemplate.header("Content-Type", "application/json");
                }
            }
        };
    }
}
