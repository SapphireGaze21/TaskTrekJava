package com.javaproject.java_project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillProgress {

    @Builder.Default
    private int level = 1;

    @Builder.Default
    private int xp = 0;

    private String courseName; // Name of the course this progress tracks

    @Builder.Default
    private String levelName = "Novice Scholar";
}