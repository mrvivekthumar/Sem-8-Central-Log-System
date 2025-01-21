package com.example.studentservice.Model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    private String name;
    @Column(unique = true)
    private String email;
    private int roll_no;
    private String department;
    @Enumerated(EnumType.STRING)
    private StudentAvaibility studentAvaibility=StudentAvaibility.AVAILABLE;
    private float ratings=0;


}
