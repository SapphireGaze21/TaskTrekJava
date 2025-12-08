package com.javaproject.java_project.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExamPrepTask extends Task {

    @Override
    public void configureXp() {
        this.baseXP = 220;
        this.multiplier = 3.0;
    }
}
