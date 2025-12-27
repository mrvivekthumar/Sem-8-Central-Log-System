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
@RequestMapping("/api/auth")
@Validated
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
        logger.info("Available Endpoints:");
        logger.info("  POST   /auth/register");
        logger.info("  POST   /auth/login");
        logger.info("  POST   /auth/logout");
        logger.info("  POST   /auth/refresh");
        logger.info("  GET    /auth/validate");
        logger.info("  GET    /auth/verify");
        logger.info("  GET    /auth/profile");
        logger.info("  PUT    /auth/profile");
        logger.info("  POST   /auth/password/change");
        logger.info("=================================================");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Registration attempt for user: {}", registerRequest.getEmail());
        logger.debug("Controller: Registration details - Email: {}, Role: {}, Name: {}",
                registerRequest.getEmail(), registerRequest.getRole(), registerRequest.getName());
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            if (!registerRequest.getRole().equals("STUDENT") &&
                    !registerRequest.getRole().equals("FACULTY")) {
                logger.warn("Controller: Invalid role provided: {}", registerRequest.getRole());
                throw new InvalidRequestException("Invalid role. Only STUDENT and FACULTY are allowed.");
            }

            logger.debug("Controller: Role validation passed for: {}", registerRequest.getRole());

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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Login attempt for user: {}", loginRequest.getEmail());
        logger.debug("Controller: Login request from IP: {}", request.getRemoteAddr());
        logger.debug("Controller: User-Agent: {}", request.getHeader("User-Agent"));

        try {
            logger.debug("Controller: Authenticating user credentials for: {}", loginRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                logger.info("Controller: Authentication successful for user: {}", loginRequest.getEmail());
                logger.debug("Controller: Authentication details: {}", authentication);

                logger.debug("Controller: Generating JWT token for: {}", loginRequest.getEmail());
                String token = authService.generateToken(loginRequest.getEmail());

                logger.debug("Controller: Fetching user details for: {}", loginRequest.getEmail());
                UserCredential user = authService.getUserByEmail(loginRequest.getEmail());

                UserResponse userResponse = UserResponse.builder()
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

                LoginResponse response = LoginResponse.builder()
                        .accessToken(token)
                        .refreshToken(token)
                        .expiresIn(3600L)
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Logout request received");
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            logger.info("Controller: Logout successful");
            logger.info("===============================================");

            return ResponseEntity.ok(Map.of(
                    "message", "Logged out successfully",
                    "success", true));

        } catch (Exception e) {
            logger.error("Controller: Logout error: {}", e.getMessage());
            logger.info("===============================================");
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Refresh token request received");

        try {
            String refreshToken = request.getRefreshToken();

            authService.validateToken(refreshToken);

            logger.info("Controller: Token refresh successful");
            logger.info("===============================================");

            return ResponseEntity.ok(Map.of(
                    "accessToken", refreshToken,
                    "message", "Token refreshed successfully"));

        } catch (Exception e) {
            logger.error("Controller: Token refresh failed: {}", e.getMessage());
            logger.info("===============================================");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid token"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Token verification request received");
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            String token = authHeader.replace("Bearer ", "");
            authService.validateToken(token);

            logger.info("Controller: Token verification successful");
            logger.info("===============================================");

            return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));

        } catch (Exception e) {
            logger.error("Controller: Token verification failed: {}", e.getMessage());
            logger.info("===============================================");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid token"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("X-User-Id") String userId,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: Get profile request for user ID: {}", userId);
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            UserCredential user = authService.getUserById(Long.parseLong(userId));

            UserResponse userResponse = UserResponse.builder()
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

            logger.info("Controller: Profile fetched successfully for user: {}", userId);
            logger.info("===============================================");

            return ResponseEntity.ok(userResponse);

        } catch (NumberFormatException e) {
            logger.error("Controller: Invalid user ID format: {}", userId);
            logger.info("===============================================");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid user ID format"));
        } catch (Exception e) {
            logger.error("Controller: Failed to fetch profile for user {}: {}", userId, e.getMessage());
            logger.info("===============================================");
            throw e;
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ProfileUpdateRequest profileRequest,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: Update profile request for user ID: {}", userId);
        logger.debug("Controller: Profile data: {}", profileRequest);
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            UserResponse updatedUser = authService.updateProfile(Long.parseLong(userId), profileRequest);

            logger.info("Controller: Profile updated successfully for user: {}", userId);
            logger.info("===============================================");

            return ResponseEntity.ok(updatedUser);

        } catch (NumberFormatException e) {
            logger.error("Controller: Invalid user ID format: {}", userId);
            logger.info("===============================================");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid user ID format"));
        } catch (Exception e) {
            logger.error("Controller: Failed to update profile for user {}: {}", userId, e.getMessage());
            logger.info("===============================================");
            throw e;
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ChangePasswordRequest passwordRequest,
            HttpServletRequest request) {

        logger.info("===============================================");
        logger.info("Controller: Change password request for user ID: {}", userId);
        logger.debug("Controller: Request from IP: {}", request.getRemoteAddr());

        try {
            authService.changePassword(Long.parseLong(userId), passwordRequest);

            logger.info("Controller: Password changed successfully for user: {}", userId);
            logger.info("===============================================");

            return ResponseEntity.ok(Map.of(
                    "message", "Password changed successfully",
                    "success", true));

        } catch (NumberFormatException e) {
            logger.error("Controller: Invalid user ID format: {}", userId);
            logger.info("===============================================");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid user ID format"));
        } catch (AuthenticationException e) {
            logger.error("Controller: Password change failed for user {}: {}", userId, e.getMessage());
            logger.info("===============================================");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Controller: Failed to change password for user {}: {}", userId, e.getMessage());
            logger.info("===============================================");
            throw e;
        }
    }
}
