package com.javaproject.java_project.model;

import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("QUIZPREP")
public class QuizPrepTask extends Task {

    @Override
    public void configureXp() {
        this.baseXP = 160;
        this.multiplier = 2.0;
    }
}
