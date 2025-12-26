package com.example.authenticationservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.domain.UserCredential;
import com.example.authenticationservice.dto.LoginRequest;
import com.example.authenticationservice.dto.LoginResponse;
import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.dto.UserResponse;
import com.example.authenticationservice.exception.AuthenticationException;
import com.example.authenticationservice.exception.InvalidRequestException;
import com.example.authenticationservice.service.AuthService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("AuthController Initialized and Ready!");
        logger.info("Available Endpoints: /register, /login, /validate");
        logger.info("=================================================");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Registration attempt for user: {}", registerRequest.getEmail());
        logger.debug("Controller: Registration details - Email: {}, Role: {}, Name: {}",
                registerRequest.getEmail(), registerRequest.getRole(), registerRequest.getName());
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            // Validate role
            if (!registerRequest.getRole().equals("STUDENT") &&
                    !registerRequest.getRole().equals("FACULTY")) {
                logger.warn("Controller: Invalid role provided: {}", registerRequest.getRole());
                throw new InvalidRequestException("Invalid role. Only STUDENT and FACULTY are allowed.");
            }

            logger.debug("Controller: Role validation passed for: {}", registerRequest.getRole());

            // Create user credential
            UserResponse userResponse = authService.register(registerRequest);

            logger.info("Controller: Registration successful for user: {} with ID: {}",
                    registerRequest.getEmail(), userResponse.getId());
            logger.debug("Controller: User response: {}", userResponse);
            logger.info("===============================================");

            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

        } catch (InvalidRequestException e) {
            logger.error("Controller: Registration validation failed for {}: {}",
                    registerRequest.getEmail(), e.getMessage());
            logger.info("===============================================");
            throw e;
        } catch (Exception e) {
            logger.error("Controller: Unexpected error during registration for {}: {}",
                    registerRequest.getEmail(), e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Login attempt for user: {}", loginRequest.getEmail());
        logger.debug("Controller: Login request from IP: {}", request.getRemoteAddr());
        logger.debug("Controller: User-Agent: {}", request.getHeader("User-Agent"));

        try {
            // Authenticate user
            logger.debug("Controller: Authenticating user credentials for: {}", loginRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                logger.info("Controller: Authentication successful for user: {}", loginRequest.getEmail());
                logger.debug("Controller: Authentication details: {}", authentication);

                // Generate token
                logger.debug("Controller: Generating JWT token for: {}", loginRequest.getEmail());
                String token = authService.generateToken(loginRequest.getEmail());

                // Get user details
                logger.debug("Controller: Fetching user details for: {}", loginRequest.getEmail());
                UserCredential user = authService.getUserByEmail(loginRequest.getEmail());

                // Build user DTO
                UserResponse userResponse = new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRole());

                // Build response
                LoginResponse response = LoginResponse.builder()
                        .accessToken(token)
                        .expiresIn(3600) // 1 hour
                        .user(userResponse)
                        .build();

                logger.info("Controller: Login successful for user: {}, Role: {}, ID: {}",
                        loginRequest.getEmail(), user.getRole(), user.getId());
                logger.debug("Controller: Token length: {} characters", token.length());
                logger.info("===============================================");

                return ResponseEntity.ok(response);
            } else {
                logger.warn("Controller: Authentication failed for user: {} - Not authenticated",
                        loginRequest.getEmail());
                logger.info("===============================================");
                throw new AuthenticationException("Invalid credentials");
            }

        } catch (BadCredentialsException e) {
            logger.error("Controller: Login failed for user: {} - Bad credentials", loginRequest.getEmail());
            logger.info("===============================================");
            throw new AuthenticationException("Invalid email or password");
        } catch (AuthenticationException e) {
            logger.error("Controller: Login failed for user: {} - {}",
                    loginRequest.getEmail(), e.getMessage());
            logger.info("===============================================");
            throw e;
        } catch (Exception e) {
            logger.error("Controller: Unexpected error during login for {}: {}",
                    loginRequest.getEmail(), e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Token validation request received");
        logger.debug("Controller: Token preview: {}...", token.substring(0, Math.min(30, token.length())));
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            authService.validateToken(token);

            logger.info("Controller: Token validation successful");
            logger.info("===============================================");

            return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));

        } catch (Exception e) {
            logger.error("Controller: Token validation failed: {}", e.getMessage());
            logger.info("===============================================");
            throw e;
        }
    }
}
