package com.example.adminservice.Model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Student {
    private int s_id;
    private String password;
    private String name;
    private String email;


}

