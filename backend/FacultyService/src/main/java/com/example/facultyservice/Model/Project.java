package com.example.facultyservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int p_id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status=Status.NEW;
    private LocalDate date=LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "facultyId")
    private Faculty faculty;






}