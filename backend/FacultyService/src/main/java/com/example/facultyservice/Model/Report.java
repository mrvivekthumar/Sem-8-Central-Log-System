package com.example.facultyservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;
    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;
    private String fileName;
    private String contentType;
    private LocalDateTime submissionDate;
    @OneToOne
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;
}
