package com.javaproject.java_project.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Data

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class Task {
    private int taskID;
    private int courseID;
    private String title;
    private String description;
    protected int baseXP;
    protected double multiplier;
    private boolean completed;
    private LocalDateTime deadline;

    public void configureXp() {
        // Generic ungraded task
        this.baseXP = 50;
        this.multiplier = 1.0;
    }

}
