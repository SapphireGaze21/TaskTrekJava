package com.javaproject.java_project.request;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LoginRequest {
    private String username;
    private String password;
}
