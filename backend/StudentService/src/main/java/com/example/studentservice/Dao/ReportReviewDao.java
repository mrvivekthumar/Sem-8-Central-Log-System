package com.example.studentservice.Dao;

import com.example.studentservice.Model.ReportReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportReviewDao extends JpaRepository<ReportReview, Integer> {
    Optional<List<ReportReview>> findByReport_ReportId(int reportId);

    Optional<ReportReview> findByReport_ReportIdAndReviewedBy_StudentId(int reportId, int studentId);
}
