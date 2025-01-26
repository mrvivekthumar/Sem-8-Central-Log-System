package com.example.authenticationservice.Service;

import com.example.authenticationservice.Dao.UserCredentialDao;
import com.example.authenticationservice.Model.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    @Autowired
    private UserCredentialDao userCredentialDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public String saveUser(List<UserCredential> users){
        users.parallelStream().forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        });

        userCredentialDao.saveAll(users);
        return "Users saved";
    }
    public String generateToken(String username, String userRole) {
        UserCredential user = userCredentialDao.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (!user.getUserRole().name().equalsIgnoreCase(userRole)) {
            throw new RuntimeException("Unauthorized role");
        }

        return jwtService.generateToken(username, userRole);
    }
    public boolean validateToken(String token, String userRole) {
        return jwtService.validateToken(token, userRole);
    }
}
