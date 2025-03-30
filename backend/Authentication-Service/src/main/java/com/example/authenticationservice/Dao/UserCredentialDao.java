package com.example.authenticationservice.Dao;

import com.example.authenticationservice.Model.UserCredential;
import com.example.authenticationservice.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialDao extends JpaRepository<UserCredential,Integer> {
    Optional<UserCredential> findByUsername(String username);

    Optional<UserCredential> findByUsernameAndUserRole(String username, UserRole userRole);
}
