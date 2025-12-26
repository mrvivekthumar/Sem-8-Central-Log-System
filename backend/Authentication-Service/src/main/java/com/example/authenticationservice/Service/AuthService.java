package com.example.authenticationservice.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.domain.UserCredential;
import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.dto.UserResponse;
import com.example.authenticationservice.exception.AuthenticationException;
import com.example.authenticationservice.exception.InvalidRequestException;
import com.example.authenticationservice.repository.UserCredentialRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostConstruct
    public void init() {
        logger.info("AuthService initialized and ready");
    }

    public String saveUser(List<UserCredential> credentials) {
        logger.info("Bulk user registration initiated for {} users", credentials.size());
        logger.debug("User credentials to register: {}", credentials);

        try {
            int registered = 0;
            int skipped = 0;

            for (UserCredential credential : credentials) {
                if (!userCredentialRepository.existsByEmail(credential.getEmail())) {
                    credential.setPassword(passwordEncoder.encode(credential.getPassword()));
                    userCredentialRepository.save(credential);
                    registered++;
                    logger.debug("User registered: {}", credential.getEmail());
                } else {
                    skipped++;
                    logger.debug("User already exists, skipped: {}", credential.getEmail());
                }
            }

            logger.info("Bulk registration completed - Registered: {}, Skipped: {}", registered, skipped);
            return "Users registered successfully - Registered: " + registered + ", Skipped: " + skipped;

        } catch (Exception e) {
            logger.error("Error in bulk user registration: {}", e.getMessage(), e);
            return "Failed to register users: " + e.getMessage();
        }
    }

    public UserResponse register(RegisterRequest request) {
        logger.info("Service: User registration initiated for email: {}", request.getEmail());
        logger.debug("Service: Registration request details - Email: {}, Role: {}, Name: {}",
                request.getEmail(), request.getRole(), request.getName());

        try {
            if (userCredentialRepository.existsByEmail(request.getEmail())) {
                logger.warn("Service: Registration failed - User already exists: {}", request.getEmail());
                throw new InvalidRequestException("User with this email already exists");
            }

            if (!request.getRole().equals("STUDENT") && !request.getRole().equals("FACULTY")) {
                logger.warn("Service: Invalid role provided: {}", request.getRole());
                throw new InvalidRequestException("Invalid role");
            }

            logger.debug("Service: Creating new user credential for: {}", request.getEmail());
            UserCredential user = new UserCredential();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setName(request.getName());

            logger.debug("Service: Saving user to database: {}", request.getEmail());
            UserCredential saved = userCredentialRepository.save(user);

            logger.info("Service: User registered successfully - ID: {}, Email: {}, Role: {}",
                    saved.getId(), saved.getEmail(), saved.getRole());

            return new UserResponse(
                    saved.getId(),
                    saved.getEmail(),
                    saved.getRole());

        } catch (InvalidRequestException e) {
            logger.error("Service: Registration validation failed for {}: {}",
                    request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Service: Unexpected error during registration for {}: {}",
                    request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    public String generateToken(String email) {
        logger.info("Service: Generating JWT token for user: {}", email);

        try {
            UserCredential user = getUserByEmail(email);
            logger.debug("Service: User found for token generation - ID: {}, Role: {}",
                    user.getId(), user.getRole());

            String token = jwtService.generateToken(user);
            logger.info("Service: JWT token generated successfully for user: {}", email);
            logger.debug("Service: Token preview: {}...", token.substring(0, Math.min(20, token.length())));

            return token;
        } catch (Exception e) {
            logger.error("Service: Failed to generate token for user {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    public void validateToken(String token) {
        logger.info("Service: Validating JWT token");
        logger.debug("Service: Token to validate: {}...", token.substring(0, Math.min(20, token.length())));

        try {
            jwtService.validateToken(token);
            logger.info("Service: Token validation successful");
        } catch (Exception e) {
            logger.error("Service: Token validation failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    public UserCredential getUserByEmail(String email) {
        logger.debug("Service: Fetching user by email: {}", email);

        try {
            UserCredential user = userCredentialRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Service: User not found with email: {}", email);
                        return new RuntimeException("User not found");
                    });

            logger.debug("Service: User found - ID: {}, Email: {}, Role: {}",
                    user.getId(), user.getEmail(), user.getRole());
            return user;
        } catch (Exception e) {
            logger.error("Service: Error fetching user by email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    public void updatePassword(UserCredential credential) {
        logger.info("Service: Password update initiated for user: {}", credential.getEmail());

        try {
            UserCredential user = userCredentialRepository.findByEmail(credential.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("Service: Password update failed - User not found: {}", credential.getEmail());
                        return new AuthenticationException("User not found");
                    });

            logger.debug("Service: Encoding new password for user: {}", credential.getEmail());
            user.setPassword(passwordEncoder.encode(credential.getPassword()));
            userCredentialRepository.save(user);

            logger.info("Service: Password updated successfully for user: {}", credential.getEmail());
        } catch (AuthenticationException e) {
            logger.error("Service: Password update failed for {}: {}",
                    credential.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Service: Unexpected error during password update for {}: {}",
                    credential.getEmail(), e.getMessage(), e);
            throw e;
        }
    }
}
