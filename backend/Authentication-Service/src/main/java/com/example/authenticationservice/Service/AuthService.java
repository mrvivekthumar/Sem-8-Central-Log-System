package com.example.authenticationservice.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.domain.UserCredential;
import com.example.authenticationservice.dto.ChangePasswordRequest;
import com.example.authenticationservice.dto.ProfileUpdateRequest;
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

            // Initialize default values
            user.setSkills(new ArrayList<>());
            user.setRatings(0.0);
            user.setProjectsCompleted(0);
            user.setCurrentProjects(0);

            logger.debug("Service: Saving user to database: {}", request.getEmail());
            UserCredential saved = userCredentialRepository.save(user);

            logger.info("Service: User registered successfully - ID: {}, Email: {}, Role: {}",
                    saved.getId(), saved.getEmail(), saved.getRole());

            return UserResponse.builder()
                    .id(saved.getId())
                    .email(saved.getEmail())
                    .role(saved.getRole())
                    .name(saved.getName())
                    .bio(saved.getBio())
                    .skills(saved.getSkills())
                    .githubProfileLink(saved.getGithubProfileLink())
                    .linkedInProfileLink(saved.getLinkedInProfileLink())
                    .portfolioLink(saved.getPortfolioLink())
                    .phone(saved.getPhone())
                    .location(saved.getLocation())
                    .ratings(saved.getRatings())
                    .projectsCompleted(saved.getProjectsCompleted())
                    .currentProjects(saved.getCurrentProjects())
                    .build();

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

    public UserCredential getUserById(Long id) {
        logger.debug("Service: Fetching user by ID: {}", id);

        try {
            UserCredential user = userCredentialRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Service: User not found with ID: {}", id);
                        return new RuntimeException("User not found");
                    });

            logger.debug("Service: User found - ID: {}, Email: {}, Role: {}",
                    user.getId(), user.getEmail(), user.getRole());
            return user;
        } catch (Exception e) {
            logger.error("Service: Error fetching user by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public UserResponse updateProfile(Long userId, ProfileUpdateRequest profileRequest) {
        logger.info("Service: Profile update initiated for user ID: {}", userId);

        try {
            UserCredential user = userCredentialRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("Service: Profile update failed - User not found: {}", userId);
                        return new AuthenticationException("User not found");
                    });

            // Check if email is being changed and if it's already taken
            if (!user.getEmail().equals(profileRequest.getEmail()) &&
                    userCredentialRepository.existsByEmail(profileRequest.getEmail())) {
                logger.warn("Service: Email already exists: {}", profileRequest.getEmail());
                throw new InvalidRequestException("Email already in use");
            }

            logger.debug("Service: Updating profile fields for user: {}", userId);

            // Update all fields
            user.setName(profileRequest.getName());
            user.setEmail(profileRequest.getEmail());
            user.setBio(profileRequest.getBio());
            user.setSkills(profileRequest.getSkills() != null ? profileRequest.getSkills() : new ArrayList<>());
            user.setGithubProfileLink(profileRequest.getGithubProfileLink());
            user.setLinkedInProfileLink(profileRequest.getLinkedInProfileLink());
            user.setPortfolioLink(profileRequest.getPortfolioLink());
            user.setPhone(profileRequest.getPhone());
            user.setLocation(profileRequest.getLocation());

            // Update stats if provided
            if (profileRequest.getRatings() != null) {
                user.setRatings(profileRequest.getRatings());
            }
            if (profileRequest.getProjectsCompleted() != null) {
                user.setProjectsCompleted(profileRequest.getProjectsCompleted());
            }
            if (profileRequest.getCurrentProjects() != null) {
                user.setCurrentProjects(profileRequest.getCurrentProjects());
            }

            UserCredential saved = userCredentialRepository.save(user);

            logger.info("Service: Profile updated successfully for user: {}", userId);

            return UserResponse.builder()
                    .id(saved.getId())
                    .email(saved.getEmail())
                    .role(saved.getRole())
                    .name(saved.getName())
                    .bio(saved.getBio())
                    .skills(saved.getSkills())
                    .githubProfileLink(saved.getGithubProfileLink())
                    .linkedInProfileLink(saved.getLinkedInProfileLink())
                    .portfolioLink(saved.getPortfolioLink())
                    .phone(saved.getPhone())
                    .location(saved.getLocation())
                    .ratings(saved.getRatings())
                    .projectsCompleted(saved.getProjectsCompleted())
                    .currentProjects(saved.getCurrentProjects())
                    .build();

        } catch (InvalidRequestException | AuthenticationException e) {
            logger.error("Service: Profile update failed for {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Service: Unexpected error during profile update for {}: {}",
                    userId, e.getMessage(), e);
            throw e;
        }
    }

    public void changePassword(Long userId, ChangePasswordRequest passwordRequest) {
        logger.info("Service: Change password initiated for user ID: {}", userId);

        try {
            UserCredential user = userCredentialRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("Service: Password change failed - User not found: {}", userId);
                        return new AuthenticationException("User not found");
                    });

            // Verify current password
            if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
                logger.warn("Service: Current password mismatch for user: {}", userId);
                throw new AuthenticationException("Current password is incorrect");
            }

            logger.debug("Service: Encoding new password for user: {}", userId);
            user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            userCredentialRepository.save(user);

            logger.info("Service: Password changed successfully for user: {}", userId);
        } catch (AuthenticationException e) {
            logger.error("Service: Password change failed for {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Service: Unexpected error during password change for {}: {}",
                    userId, e.getMessage(), e);
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
