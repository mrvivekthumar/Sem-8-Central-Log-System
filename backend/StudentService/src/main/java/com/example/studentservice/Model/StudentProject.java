package com.example.studentservice.Model;

import com.example.studentservice.Vo.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_project")
public class StudentProject {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int applicationId;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Student student;
    @Column(name = "project_id", nullable = false)
    private int projectId;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name="application_date")
    private LocalDate applicationDate;
    @Column(name="preference")
    private int preference;  // New field for ranking
    @OneToOne(mappedBy = "studentProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Report report;  // Each project has one report at a time
}
