package com.example.authenticationservice.Controller;

import com.example.authenticationservice.Dto.AuthRequest;
import com.example.authenticationservice.Model.UserCredential;
import com.example.authenticationservice.Service.AuthService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;
    @Autowired
    //for authentication
    private AuthenticationManager authenticationManager;
    @PostMapping("registerOne")
    public ResponseEntity<UserCredential> addSingleOne(@RequestBody UserCredential user){
        return authService.saveOneUser(user);
    }
    @PostMapping("register")
    public String addNewUser(@RequestBody List<UserCredential> users){
        return authService.saveUser(users);
    }
    @PostMapping("updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody UserCredential user){
        return authService.updatePassword(user);

    }
    @PostConstruct
    public void init() {
        logger.info("AuthController Initialized!");
    }
    @PostMapping("token")
    public ResponseEntity<Map<String, String>> getToken(@RequestBody AuthRequest authRequest) {
        System.out.println("hello");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = authService.generateToken(authRequest.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response); // Return token in a JSON response
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Invalid credentials"));
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam String token) {
        if (authService.validateToken(token)) {
            return "Token is valid";
        } else {
            throw new RuntimeException("Invalid or expired token");
        }
    }
    @GetMapping("/user")
    public ResponseEntity<UserCredential> getUserDetails(@RequestParam("token") String token) {

        System.out.println("Hello");
        return authService.getUserdetails(token);
    }
    @GetMapping("hello")
    public String hello(){
        return "hello world";
    }

}