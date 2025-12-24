package com.example.studentservice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private int projectId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate date;
    private List<String> skills;
    @ManyToOne
    @JoinColumn(name = "facultyId")
    private Faculty faculty;
    private LocalDateTime deadline;


}
