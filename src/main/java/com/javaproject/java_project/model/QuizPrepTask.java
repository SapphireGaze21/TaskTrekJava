package com.javaproject.java_project.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QuizPrepTask extends Task {

    @Override
    public void configureXp() {
        this.baseXP = 160;
        this.multiplier = 2.0;
    }
}
