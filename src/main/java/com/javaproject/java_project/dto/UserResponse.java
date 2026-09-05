package com.javaproject.java_project.dto;

import com.javaproject.java_project.model.User;

import java.time.LocalDate;

/** A safe, frontend-facing representation of an authenticated user. */
public record UserResponse(
        Long id,
        String username,
        int level,
        int xp,
        int streak,
        LocalDate lastTaskCompletedDate
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getUsername(), user.getLevel(), user.getXp(),
                user.getStreak(), user.getLastTaskCompletedDate()
        );
    }
}
