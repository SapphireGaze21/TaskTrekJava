package com.javaproject.java_project.model;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Achievement {

    private int id;
    private String title;
    private String description;

    @Builder.Default
    private boolean unlocked = false;

    @Builder.Default
    private LocalDate unlockedDate = null;
}
