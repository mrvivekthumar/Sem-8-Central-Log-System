package com.example.facultyservice.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.facultyservice.security.JwtAuthenticationFilter;

import jakarta.annotation.PostConstruct;

/**
 * Security Configuration for Faculty Service
 * 
 * Purpose:
 * - Configure Spring Security for JWT authentication
 * - Define public and protected endpoints
 * - Set up CORS for frontend access
 * - Configure stateless session management
 * 
 * Security Flow:
 * 1. Request arrives → JwtAuthenticationFilter
 * 2. Extract JWT token from header
 * 3. Validate token and extract user info
 * 4. Set authentication in SecurityContext
 * 5. Proceed to controller if authorized
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("Security Configuration Initializing...");
        logger.info("JWT Authentication: ENABLED");
        logger.info("Session Management: STATELESS");
        logger.info("=================================================");
    }

    /**
     * Security Filter Chain
     * Defines security rules for all endpoints
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain...");

        http
                // Disable CSRF (not needed for stateless JWT authentication)
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html")
                        .permitAll()

                        // OPTIONS requests (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated())

                // Stateless session (no session cookies)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("✓ Security filter chain configured successfully");
        return http.build();
    }

    /**
     * CORS Configuration
     * Allows frontend applications to access the API
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS...");

        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://*.vercel.app"));

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174"));

        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allowed headers
        configuration.setAllowedHeaders(List.of("*"));

        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Correlation-Id",
                "X-Total-Count"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Max age for preflight cache
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        logger.info("✓ CORS configured for: localhost:3000, localhost:5173, *.vercel.app");
        return source;
    }
}
