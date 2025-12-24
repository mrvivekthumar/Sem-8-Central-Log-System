package com.example.studentservice.client.dto;

import jakarta.annotation.Nonnull;
import lombok.*;

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
