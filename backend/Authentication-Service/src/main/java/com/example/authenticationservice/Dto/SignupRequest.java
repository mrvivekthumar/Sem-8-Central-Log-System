package com.example.authenticationservice.Dto;

import com.example.authenticationservice.Model.UserRole;
import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private UserRole userRole;
    
    // Student fields
    private String enrollmentNumber;
    private String branch;
    private Integer semester;
    
    // Faculty fields
    private String department;
}
