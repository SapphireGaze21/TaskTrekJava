package com.javaproject.java_project.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupRequest {
    private String username;
    private String password;
}
