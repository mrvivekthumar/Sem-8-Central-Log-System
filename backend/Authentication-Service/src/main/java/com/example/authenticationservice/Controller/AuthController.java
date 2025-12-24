package com.example.authenticationservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
        logger.info("AuthController Initialized!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

        logger.info("Registration attempt for user: {}", registerRequest.getEmail());

        // Validate role
        if (!registerRequest.getRole().equals("STUDENT") &&
                !registerRequest.getRole().equals("FACULTY")) {
            throw new InvalidRequestException("Invalid role. Only STUDENT and FACULTY are allowed.");

        }

        // Create user credential
        UserResponse userResponse = authService.register(registerRequest);

        logger.info("Registration successful for user: {}", registerRequest.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        logger.info("Login attempt for user: {}", loginRequest.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            // Generate token
            String token = authService.generateToken(loginRequest.getEmail());

            // Get user details
            UserCredential user = authService.getUserByEmail(loginRequest.getEmail());

            // Build user DTO
            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole());

            // Build response
            LoginResponse response = LoginResponse.builder()
                    .accessToken(token)
                    .expiresIn(3600) // or from config
                    .user(userResponse)
                    .build();

            logger.info("Login successful for user: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } else {
            throw new AuthenticationException("Invalid credentials");
        }

    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        authService.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));
    }
}