package com.example.facultyservice.Vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredential {
    private int id;
    private String username;
    private String password;
    private UserRole userRole;


}
