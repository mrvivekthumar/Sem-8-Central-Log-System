package com.example.studentservice.Model;

import jakarta.annotation.Nullable;
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
    @Transient
    private String password;
    private String email;
    private String githubProfileLink;
    private List<String>Skills=new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private StudentAvaibility studentAvaibility=StudentAvaibility.AVAILABLE;
    @Nullable
    private float ratings=0;
    @Nullable
    private String bio;
    private Float cgpa;
    private String phoneNo;
    @Nullable
    private Integer semesterNo;
    @Nullable
    private String imageUrl;
    @Nullable
    private String linkedInUrl;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<PersonalProject> projects = new ArrayList<>();



}
