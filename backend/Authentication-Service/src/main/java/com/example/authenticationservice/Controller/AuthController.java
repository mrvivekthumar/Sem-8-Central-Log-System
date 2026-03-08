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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.domain.UserCredential;
import com.example.authenticationservice.dto.ChangePasswordRequest;
import com.example.authenticationservice.dto.LoginRequest;
import com.example.authenticationservice.dto.LoginResponse;
import com.example.authenticationservice.dto.ProfileUpdateRequest;
import com.example.authenticationservice.dto.RefreshTokenRequest;
import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.dto.UserResponse;
import com.example.authenticationservice.exception.AuthenticationException;
import com.example.authenticationservice.exception.InvalidRequestException;
import com.example.authenticationservice.service.AuthService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private static final long TOKEN_EXPIRY_SECONDS = 1800L; // 30 minutes

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostConstruct
    public void init() {
        logger.info("AuthController Initialized and Ready!");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {
        logger.info("Controller: Registration attempt for user: {}", registerRequest.getEmail());

        try {
            UserResponse userResponse = authService.register(registerRequest);

            logger.info("Controller: Registration successful for user: {} with ID: {}",
                    registerRequest.getEmail(), userResponse.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

        } catch (InvalidRequestException e) {
            logger.error("Controller: Registration validation failed for {}: {}",
                    registerRequest.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Controller: Unexpected error during registration for {}: {}",
                    registerRequest.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        logger.info("Controller: Login attempt for user: {}", loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                logger.info("Controller: Authentication successful for user: {}", loginRequest.getEmail());

                String token = authService.generateToken(loginRequest.getEmail());
                UserCredential user = authService.getUserByEmail(loginRequest.getEmail());
                UserResponse userResponse = toUserResponse(user);

                LoginResponse response = LoginResponse.builder()
                        .accessToken(token)
                        .refreshToken(token)
                        .expiresIn(TOKEN_EXPIRY_SECONDS)
                        .user(userResponse)
                        .build();

                logger.info("Controller: Login successful for user: {}, Role: {}, ID: {}",
                        loginRequest.getEmail(), user.getRole(), user.getId());

                return ResponseEntity.ok(response);
            } else {
                logger.warn("Controller: Authentication failed for user: {} - Not authenticated",
                        loginRequest.getEmail());
                throw new AuthenticationException("Invalid credentials");
            }

        } catch (BadCredentialsException e) {
            logger.error("Controller: Login failed for user: {} - Bad credentials", loginRequest.getEmail());
            throw new AuthenticationException("Invalid email or password");
        } catch (AuthenticationException e) {
            logger.error("Controller: Login failed for user: {} - {}",
                    loginRequest.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Controller: Unexpected error during login for {}: {}",
                    loginRequest.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logger.info("Controller: Logout request received");

        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "success", true));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        logger.info("Controller: Refresh token request received");

        try {
            String refreshToken = request.getRefreshToken();
            authService.validateToken(refreshToken);

            String email = authService.extractUsername(refreshToken);
            String newToken = authService.generateToken(email);

            logger.info("Controller: Token refresh successful");

            return ResponseEntity.ok(Map.of(
                    "accessToken", newToken,
                    "expiresIn", TOKEN_EXPIRY_SECONDS,
                    "message", "Token refreshed successfully"));

        } catch (Exception e) {
            logger.error("Controller: Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, HttpServletRequest request) {
        logger.info("Controller: Token validation request received");

        try {
            authService.validateToken(token);

            logger.info("Controller: Token validation successful");

            return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));

        } catch (Exception e) {
            logger.error("Controller: Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid token"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) {
        logger.info("Controller: Token verification request received");

        try {
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX) || authHeader.length() <= BEARER_PREFIX.length()) {
                throw new AuthenticationException("Invalid Authorization header");
            }
            String token = authHeader.substring(BEARER_PREFIX.length());
            authService.validateToken(token);

            logger.info("Controller: Token verification successful");

            return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));

        } catch (Exception e) {
            logger.error("Controller: Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid token"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("X-User-Id") String userId,
            HttpServletRequest request) {
        logger.info("Controller: Get profile request for user ID: {}", userId);

        try {
            UserCredential user = authService.getUserById(Long.parseLong(userId));
            UserResponse userResponse = toUserResponse(user);

            logger.info("Controller: Profile fetched successfully for user: {}", userId);

            return ResponseEntity.ok(userResponse);

        } catch (NumberFormatException e) {
            logger.error("Controller: Invalid user ID format: {}", userId);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid user ID format"));
        } catch (Exception e) {
            logger.error("Controller: Failed to fetch profile for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ProfileUpdateRequest profileRequest,
            HttpServletRequest request) {

        logger.info("Controller: Update profile request for user ID: {}", userId);

        try {
            UserResponse updatedUser = authService.updateProfile(Long.parseLong(userId), profileRequest);

            logger.info("Controller: Profile updated successfully for user: {}", userId);

            return ResponseEntity.ok(updatedUser);

        } catch (NumberFormatException e) {
            logger.error("Controller: Invalid user ID format: {}", userId);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid user ID format"));
        } catch (Exception e) {
            logger.error("Controller: Failed to update profile for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ChangePasswordRequest passwordRequest,
            HttpServletRequest request) {

        logger.info("Controller: Change password request for user ID: {}", userId);

        try {
            authService.changePassword(Long.parseLong(userId), passwordRequest);

            logger.info("Controller: Password changed successfully for user: {}", userId);

            return ResponseEntity.ok(Map.of(
                    "message", "Password changed successfully",
                    "success", true));

        } catch (NumberFormatException e) {
            logger.error("Controller: Invalid user ID format: {}", userId);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid user ID format"));
        } catch (AuthenticationException e) {
            logger.error("Controller: Password change failed for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Controller: Failed to change password for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    private UserResponse toUserResponse(UserCredential user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .bio(user.getBio())
                .skills(user.getSkills())
                .githubProfileLink(user.getGithubProfileLink())
                .linkedInProfileLink(user.getLinkedInProfileLink())
                .portfolioLink(user.getPortfolioLink())
                .phone(user.getPhone())
                .location(user.getLocation())
                .ratings(user.getRatings())
                .projectsCompleted(user.getProjectsCompleted())
                .currentProjects(user.getCurrentProjects())
                .build();
    }
}
