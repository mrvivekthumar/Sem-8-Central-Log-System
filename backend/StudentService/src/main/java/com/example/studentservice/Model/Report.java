package com.example.studentservice.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "application_id", nullable = false)
    private StudentProject studentProject;  // Link to student's project application
    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private Student submittedBy;  // Student who uploaded the report

    private String documentUrl;  // Link to the report file

    private LocalDate submissionDate;  // Date when the report was submitted

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;  // PENDING, APPROVED, NEEDS_RESUBMISSION

    private String feedback;  // Faculty feedback for resubmission
    private boolean isFinalSubmission = false;  // False when under student review, True when sent to faculty
}

