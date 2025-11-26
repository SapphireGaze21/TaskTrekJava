package com.javaproject.java_project.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class Task {
    @Id
    private int taskID;
    private String title;
    private String Description;
    private int baseXP;
    private double multiplier;
}
