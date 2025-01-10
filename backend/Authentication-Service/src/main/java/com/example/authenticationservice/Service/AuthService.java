package com.example.authenticationservice.Service;

import com.example.authenticationservice.Dao.UserCredentialDao;
import com.example.authenticationservice.Model.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String generateToken(String username){
        return jwtService.generateToken(username);
    }
    public void validateToken(String token){
        jwtService.validateToken(token);
    }
}
