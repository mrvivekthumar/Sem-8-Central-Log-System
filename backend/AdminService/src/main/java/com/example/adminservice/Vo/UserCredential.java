package com.example.adminservice.Vo;
import lombok.*;

@Getter
@Setter
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential {

    private int id;
    private String username;
    private String password;
    private UserRole userRole;


}
