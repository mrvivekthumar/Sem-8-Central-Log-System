package com.example.facultyservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    
    private String name;
    private String email;
    private String enrollmentNumber;
    private String branch;
    private Integer semester;
    
    @Enumerated(EnumType.STRING)
    private StudentAvaibility studentAvaibility;
}
