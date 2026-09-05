package com.javaproject.java_project.dto;

import java.time.Instant;

/** Consistent error shape for all API endpoints. */
public record ApiError(Instant timestamp, int status, String error, String message, String path) { }
