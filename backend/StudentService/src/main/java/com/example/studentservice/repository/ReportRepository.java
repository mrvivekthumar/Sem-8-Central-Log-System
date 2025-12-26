package com.example.studentservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.studentservice.domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query("SELECT r FROM Report r WHERE r.studentProject.projectId = :projectId")
    Optional<Report> findReportByProjectId(@Param("projectId") int projectId);
}
