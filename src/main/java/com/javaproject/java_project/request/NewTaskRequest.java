package com.javaproject.java_project.request;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewTaskRequest
{
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String taskType;
}
