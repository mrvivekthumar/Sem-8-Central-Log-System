package com.example.studentservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private String githubProfileLink;
    private List<String>Skills=new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private StudentAvaibility studentAvaibility=StudentAvaibility.AVAILABLE;
    private float ratings=0;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<PersonalProject> projects = new ArrayList<>();


}
