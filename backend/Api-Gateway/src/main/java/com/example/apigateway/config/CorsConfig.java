package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Use allowedOriginPatterns instead of allowedOrigins for wildcard support
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
                "https://*.vercel.app",
                "http://localhost:*"
        ));

        // Specific origins
        corsConfig.setAllowedOrigins(Arrays.asList(
                "https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app",
                "https://colab-bridge-hcz38o2w5-hetbhagatji09-gmailcoms-projects.vercel.app",
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        // Allow all methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Allow all headers
        corsConfig.setAllowedHeaders(Arrays.asList("*"));

        // Set credentials
        corsConfig.setAllowCredentials(true);

        // Expose headers that frontend might need
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}