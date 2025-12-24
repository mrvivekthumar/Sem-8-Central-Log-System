package com.example.studentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studentservice.domain.ReportReview;

import java.util.List;
import java.util.Optional;

public interface ReportReviewDao extends JpaRepository<ReportReview, Integer> {
    Optional<List<ReportReview>> findByReport_ReportId(int reportId);

    Optional<ReportReview> findByReport_ReportIdAndReviewedBy_StudentId(int reportId, int studentId);
}
