package com.example.adminservice.Model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Data
@Getter
@Setter

public class Admin {

    private int adminId;
    private String email;
    private String password;
}
