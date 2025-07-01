package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Add your frontend URLs
        corsConfig.addAllowedOrigin("https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app");
        corsConfig.addAllowedOrigin("http://colab-bridge-hcz38o2w5-hetbhagatji09-gmailcoms-projects.vercel.app");

        // Add localhost for development (if needed)
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedOrigin("http://localhost:5173");

        // Specify allowed methods explicitly
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedMethod("HEAD");
        corsConfig.addAllowedMethod("PATCH");

        // Allow all headers
        corsConfig.addAllowedHeader("*");

        // Set credentials - ONLY ONCE
        corsConfig.setAllowCredentials(true);

        // Expose headers that frontend might need
        corsConfig.addExposedHeader("Authorization");
        corsConfig.addExposedHeader("Content-Type");

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}