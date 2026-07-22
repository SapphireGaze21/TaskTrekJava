package com.javaproject.java_project.model;

import lombok.*;
import org.springframework.stereotype.Component;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Component
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String passwordHash;

    @Builder.Default
    private int level = 1;

    @Builder.Default
    private int xp = 0;

    @Builder.Default
    private int streak = 0;

    @Builder.Default
    private LocalDate lastActiveDate = LocalDate.now();

    @Builder.Default
    private LocalDate lastTaskCompletedDate = null;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Course> userCourses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Achievement> achievements = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> inventory = new ArrayList<>(); // avatars/themes/boosters-LOGIC PENDING

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "courseName")
    @Builder.Default
    private Map<String, SkillProgress> skillProgress = new HashMap<>(); // courseName → SkillProgress
}
