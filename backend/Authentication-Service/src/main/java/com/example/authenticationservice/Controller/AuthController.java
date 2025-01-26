package com.example.authenticationservice.Controller;

import com.example.authenticationservice.Dto.AuthRequest;
import com.example.authenticationservice.Model.UserCredential;
import com.example.authenticationservice.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    //for authentication
    private AuthenticationManager authenticationManager;

    @PostMapping("register")
    public String addNewUser(@RequestBody List<UserCredential> users){
        return authService.saveUser(users);
    }
    @PostMapping("token")
    public String getToken(@RequestBody AuthRequest authRequest){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            return authService.generateToken(authRequest.getUsername(),authRequest.getUserRole());
        }
        else{
            return "Invalid credentials";
        }

    }
    @GetMapping("/validate")
    public String validateToken(@RequestParam String token, @RequestParam String userRole) {
        if (authService.validateToken(token, userRole)) {
            return "Token is valid";
        } else {
            throw new RuntimeException("Invalid or expired token");
        }
    }

}