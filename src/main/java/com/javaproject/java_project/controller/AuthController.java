package com.javaproject.java_project.controller;

import com.javaproject.java_project.dto.AuthResponse;
import com.javaproject.java_project.dto.UserResponse;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.request.LoginRequest;
import com.javaproject.java_project.request.SignupRequest;
import com.javaproject.java_project.security.JwtService;
import com.javaproject.java_project.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest signupDetails) {
        if (isBlank(signupDetails.getUsername()) || isBlank(signupDetails.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required."));
        }
        User created = authService.registerUser(signupDetails.getUsername().trim(), signupDetails.getPassword());
        if (created == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username already exists."));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(jwtService.generateToken(created.getUsername()), UserResponse.from(created)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginCreds) {
        if (isBlank(loginCreds.getUsername()) || isBlank(loginCreds.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required."));
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginCreds.getUsername(), loginCreds.getPassword())
            );
            User loggedIn = authService.getUserByUsername(authentication.getName());
            return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(loggedIn.getUsername()), UserResponse.from(loggedIn)));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password."));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(UserResponse.from(authService.getCurrentUser()));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
