package com.example.studentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonalProjectDTO {
    private int personalProjectId;
    private String name;
    private String descreption;
    private String projectLink;
}
