package com.example.facultyservice.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fId;

    private String name;
    private String email;
    private String password;
    private String department;
}
