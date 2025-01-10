package com.example.studentservice.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import jakarta.persistence.*;

import java.time.LocalDate;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {


    private int p_id;
    private String title;
    private String description;
    private String status;
    private LocalDate date;
    private Faculty faculty;

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }



    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }



    public Project(int p_id, String title, String description, String status, LocalDate date) {
        this.p_id = p_id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = LocalDate.now();
    }

    public Project(int p_id, String title, String description, String status, LocalDate date, Faculty faculty) {
        this.p_id = p_id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Project{" +
                "p_id=" + p_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", faculty=" + faculty +
                '}';
    }

    public Project() {
        this.date=LocalDate.now();
    }
}

