package com.javaproject.java_project.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

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
