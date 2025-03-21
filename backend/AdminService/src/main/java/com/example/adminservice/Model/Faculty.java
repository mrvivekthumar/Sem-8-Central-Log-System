package com.example.adminservice.Model;


import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Faculty {
    private int f_id;
    private String name;
    private String password;
    private String email;

}

