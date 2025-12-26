package com.example.facultyservice.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.facultyservice.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter
 * 
 * Purpose:
 * - Intercept all requests
 * - Extract JWT token from Authorization header
 * - Validate token
 * - Set authentication in SecurityContext
 * 
 * Note: API Gateway already validates JWT
 * This filter extracts user info from pre-validated headers
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("Processing request: {} {}", method, path);

        // Skip public endpoints
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint detected, skipping authentication: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from header
            String token = extractTokenFromRequest(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Validate token
                jwtUtil.validateToken(token);

                // Extract user information
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                Long userId = jwtUtil.extractUserId(token);

                logger.debug("Authenticated user: username={}, role={}, userId={}",
                        username, role, userId);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Add user info to request attributes
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);
                request.setAttribute("role", role);

                logger.info("✓ Authentication successful for user: {}", username);
            }

        } catch (Exception e) {
            logger.error("❌ JWT authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Check if endpoint is public (no authentication required)
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/");
    }
}
