package com.example.studentservice.Model;

import com.example.studentservice.Vo.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
}
