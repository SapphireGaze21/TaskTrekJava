package com.javaproject.java_project.model;

import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("EXAMPREP")
public class ExamPrepTask extends Task {

    @Override
    public void configureXp() {
        this.baseXP = 220;
        this.multiplier = 3.0;
    }
}
