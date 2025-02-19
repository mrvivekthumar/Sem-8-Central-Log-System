package com.example.adminservice.Vo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential {

    private int id;
    private String username;
    private String password;
    private UserRole userRole;


}
