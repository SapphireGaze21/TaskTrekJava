package com.javaproject.java_project.service;

import com.javaproject.java_project.model.User;
import com.javaproject.java_project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class AuthService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Resolves the user authenticated for the current request by JwtFilter.
     */
    public User getCurrentUser() {
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return null;
        }
        
        return getUserByUsername(authentication.getName());
    }

    public void saveUsers() {
        User current = getCurrentUser();
        if (current != null) {
            userRepository.save(current);
        }
    }

    public User registerUser(String username, String password)
    {
        if (userRepository.existsByUsername(username)) {
            System.out.println("Username taken.");
            return null;
        }

        User newUser = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .build();

        return userRepository.save(newUser);
    }

    public User loginUser(String username, String password)
    {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("User not found");
            return null;
        }

        User user = userOpt.get();
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        } else {
            System.out.println("Incorrect password");
            return null;
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
