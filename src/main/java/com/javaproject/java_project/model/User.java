package com.javaproject.java_project.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data

@Component
public class User{

    private int id;
    private String username;
    private String passwordHash;

    @Builder.Default
    private int level = 1;
    @Builder.Default
    private int xp=0;

    @Builder.Default
    private int streak=0;
    @Builder.Default
    private LocalDate lastActiveDate = LocalDate.now();

    @Builder.Default
    List<Integer> userCourses = new ArrayList<>();

    @Builder.Default
    private List<Achievement> achievements = new ArrayList<>();
    @Builder.Default
    private List<String> inventory = new ArrayList<>(); // avatars/themes/boosters-LOGIC PENDING

    @Builder.Default
    private Map<String, SkillProgress> skillProgress = new HashMap<>(); // courseName → SkillProgress
    //have to decide method to decide the level for a course
}
