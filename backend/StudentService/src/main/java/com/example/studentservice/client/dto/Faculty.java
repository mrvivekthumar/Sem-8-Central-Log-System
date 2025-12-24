package com.example.studentservice.client.dto;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Faculty {
    @Id

    private int f_id;
    private String f_password;
    private String name;
    private String email;
    private String department;


}
