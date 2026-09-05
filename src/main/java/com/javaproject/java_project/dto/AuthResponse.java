package com.javaproject.java_project.dto;

/** Returned after registration or login. Store the token and send it as a Bearer token. */
public record AuthResponse(String token, UserResponse user) { }
