package com.javaproject.java_project.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Data
public class Course
{
    private int courseId;
    private String courseName;
    private int userId; // owner
    private List<Integer> tasks; // stored by IDs
}
