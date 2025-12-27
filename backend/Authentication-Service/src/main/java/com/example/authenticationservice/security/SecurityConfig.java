package com.example.authenticationservice.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @PostConstruct
    public void init() {
        logger.info("SecurityConfig initialized - Configuring security settings");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        logger.info("Creating RestTemplate bean");
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Security Filter Chain");

        http
                // CORS first
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/auth/registerOne",
                                "/auth/register",
                                "/auth/login",
                                "/auth/hello",
                                "/auth/token",
                                "/auth/validate",
                                "/actuator/**",
                                "/auth/actuator/**",
                                "/auth/user",
                                "/auth/updatePassword")
                        .permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated())

                // Stateless session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        logger.info("Security Filter Chain configured successfully");
        logger.debug("CSRF disabled, CORS enabled, Stateless sessions configured");

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS Configuration Source");

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow multiple origins for development and production
        List<String> allowedOrigins = List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000",
                "https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app");

        configuration.setAllowedOrigins(allowedOrigins);
        logger.info("CORS allowed origins: {}", allowedOrigins);

        List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH");
        configuration.setAllowedMethods(allowedMethods);
        logger.debug("CORS allowed methods: {}", allowedMethods);

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        logger.info("CORS Configuration Source configured successfully");
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        logger.info("Creating AuthenticationManager bean");
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("Creating UserDetailsService bean");
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Creating DaoAuthenticationProvider bean");
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        logger.info("DaoAuthenticationProvider configured with UserDetailsService and PasswordEncoder");
        return daoAuthenticationProvider;
    }
}
