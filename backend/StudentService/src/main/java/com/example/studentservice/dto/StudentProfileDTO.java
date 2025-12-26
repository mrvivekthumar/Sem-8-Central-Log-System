package com.example.studentservice.dto;

import lombok.Data;

@Data
public class StudentProfileDTO {
    private String studentId;
    private String name;
    private String email;
    private String phone;
}
