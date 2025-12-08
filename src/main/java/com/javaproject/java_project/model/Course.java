package com.javaproject.java_project.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor

public class Course
{
    private int courseId;
    private String courseName;
    private int userId; // owner
    @Builder.Default
    private List<Task> tasks = new ArrayList<>(); // stored by IDs
}
