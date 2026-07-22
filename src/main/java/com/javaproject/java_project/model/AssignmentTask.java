package com.javaproject.java_project.model;

import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("ASSIGNMENT")
public class AssignmentTask extends Task {

    @Override
    public void configureXp() {
        this.baseXP = 80;
        this.multiplier = 1.2;
    }
}
