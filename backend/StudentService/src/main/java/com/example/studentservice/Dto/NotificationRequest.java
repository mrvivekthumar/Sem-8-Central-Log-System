package com.example.studentservice.Dto;

import com.example.studentservice.Model.Student;

public class NotificationRequest {
    private int projectId;
    private Student student;

    // Constructors, Getters, and Setters
    public NotificationRequest(int projectId, Student student) {
        this.projectId = projectId;
        this.student = student;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}