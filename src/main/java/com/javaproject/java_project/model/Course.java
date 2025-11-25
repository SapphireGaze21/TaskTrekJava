package com.javaproject.java_project.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Data

@Component
public class Course
{
    private int courseId;
    private String courseName;
    private int userId; // owner
    private List<Integer> tasks; // stored by IDs
}
