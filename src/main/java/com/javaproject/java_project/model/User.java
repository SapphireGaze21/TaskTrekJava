package com.javaproject.java_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data

@Component
@Document(collection = "users")
public class User{

    @Id
    private int id;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
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
