package com.example.facultyservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status=Status.OPEN_FOR_APPLICATIONS;
    private LocalDateTime date=LocalDateTime.now();
    private List<String> skills;
    @ManyToOne
    @JoinColumn(name = "facultyId")
    private Faculty faculty;
    private LocalDateTime deadline;
    private LocalDateTime applicationDeadline;
    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private Report report;





}