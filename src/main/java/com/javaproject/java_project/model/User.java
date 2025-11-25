package com.javaproject.java_project.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data

@Component
public class User
{
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
    List<Integer> userCourses = new ArrayList<>();
}
