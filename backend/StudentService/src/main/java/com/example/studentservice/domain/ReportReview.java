package com.example.studentservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;


    @ManyToOne
    @JoinColumn(name = "reviewed_by", nullable = false)
    private Student reviewedBy;  // Student who reviewed the report

    private boolean isApproved = false;  // If the student has approved the report

}
