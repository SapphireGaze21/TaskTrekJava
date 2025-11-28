package com.javaproject.java_project.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Data

@Getter
@Setter
@Builder
@AllArgsConstructor

@Component
public class Task {
    private int taskID;
    private String title;
    private String description;
    private int baseXP;
    private double multiplier;
    private int courseID;
    private int ownerUserID;
    private boolean completed;
    private LocalDateTime deadline;
    private String difficulty;
}
