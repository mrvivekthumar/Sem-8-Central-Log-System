package com.example.studentservice.domain;

import java.time.LocalDate;

import com.example.studentservice.client.dto.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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

    // ✅ ADD: Project name field
    @Column(name = "project_name")
    private String projectName;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "preference")
    private int preference; // New field for ranking

    @OneToOne(mappedBy = "studentProject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Report report; // Each project has one report at a time
}
