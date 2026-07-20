package com.javaproject.java_project.controller;

import com.javaproject.java_project.request.LoginRequest;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.request.SignupRequest;
import com.javaproject.java_project.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.javaproject.java_project.security.JwtService;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/auth")
public class AuthController 
{
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService)
    {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@org.jetbrains.annotations.NotNull @RequestBody SignupRequest signupDetails)
    {
        // trim will take care of strings with just spaces (they are empty)
        if (signupDetails.getUsername() == null || signupDetails.getUsername().trim().isEmpty())
            return new ResponseEntity<>("Username cannot be empty", HttpStatus.BAD_REQUEST);

        if (signupDetails.getPassword() == null || signupDetails.getPassword().trim().isEmpty())
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        
        User created = authService.registerUser(signupDetails.getUsername(), signupDetails.getPassword());

        if (created == null)
            return new ResponseEntity<>( "Username already exists.", HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@org.jetbrains.annotations.NotNull @RequestBody LoginRequest loginCreds)
    {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginCreds.getUsername(), loginCreds.getPassword())
            );
            
            User loggedIn = authService.getUserByUsername(authentication.getName());
            
            String token = jwtService.generateToken(loggedIn.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedIn);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
