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

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String saveUser(List<UserCredential> credentials) {
        try {
            for (UserCredential credential : credentials) {
                if (!userCredentialRepository.existsByEmail(credential.getEmail())) {
                    credential.setPassword(passwordEncoder.encode(credential.getPassword()));
                    userCredentialRepository.save(credential);
                }
            }
            return "Users registered successfully";
        } catch (Exception e) {
            logger.error("Error in bulk user registration", e);
            return "Failed to register users: " + e.getMessage();
        }
    }

    public UserResponse register(RegisterRequest request) {

        if (userCredentialRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("User with this email already exists");
        }

        if (!request.getRole().equals("STUDENT") && !request.getRole().equals("FACULTY")) {
            throw new InvalidRequestException("Invalid role");
        }

        UserCredential user = new UserCredential();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setName(request.getName());

        UserCredential saved = userCredentialRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole());
    }

    public String generateToken(String email) {
        UserCredential user = getUserByEmail(email);
        return jwtService.generateToken(user);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public UserCredential getUserByEmail(String email) {
        return userCredentialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updatePassword(UserCredential credential) {
        UserCredential user = userCredentialRepository.findByEmail(credential.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        user.setPassword(passwordEncoder.encode(credential.getPassword()));
        userCredentialRepository.save(user);
    }

}
