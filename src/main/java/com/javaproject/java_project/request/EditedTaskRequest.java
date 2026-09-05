package com.javaproject.java_project.request;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditedTaskRequest
{
    @JsonAlias("name")
    private String title;
    private String description;
    private LocalDateTime deadline;

}
