package com.javaproject.java_project.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor

@Component
public class Course
{
    private int courseId;
    private String courseName;
    private int userId; // owner
    @Builder.Default
    private List<Integer> tasks = new ArrayList<>(); // stored by IDs
}
