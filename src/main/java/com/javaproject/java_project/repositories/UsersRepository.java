package com.javaproject.java_project.repositories;

import com.javaproject.java_project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<User, Integer> {
    User findByUsername(String username);
    String returnPasswordHash(String username);
    boolean usernameExists(String username);
}
