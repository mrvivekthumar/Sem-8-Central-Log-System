package com.example.authenticationservice.Service;

import com.example.authenticationservice.Dao.UserCredentialDao;
import com.example.authenticationservice.Model.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public String generateToken(String username) {
        UserCredential user = userCredentialDao.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User not found"));


        return jwtService.generateToken(username);
    }
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    public ResponseEntity<String> updatePassword(UserCredential user) {
        UserCredential existingUser=userCredentialDao.findByUsername(user.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userCredentialDao.save(existingUser);
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);

    }
    public ResponseEntity<UserCredential> getUserdetails(String token) {
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        System.out.println("Fuck You Bro");
        String username = jwtService.extractUsername(token);
        UserCredential user = jwtService.getUserByUsername(username);
        return ResponseEntity.ok(user);


    }

    public ResponseEntity<UserCredential> saveOneUser(UserCredential user) {
        try {
            Optional<UserCredential> existingUser = userCredentialDao.findByUsernameAndUserRole(user.getUsername(),user.getUserRole());
            if (existingUser.isPresent()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userCredentialDao.save(user);
            return new ResponseEntity<>(user,HttpStatus.OK);
        } catch (Exception e) {
             // Print real error
            System.out.println();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
