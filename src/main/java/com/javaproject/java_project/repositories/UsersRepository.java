package com.javaproject.java_project.repositories;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.Task;
import com.javaproject.java_project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends MongoRepository<User, Integer> {
    User findByUsername(String username);
    String returnPasswordHash(String username);
    boolean usernameExists(String username);

    List<Course> findCourses(int userID);
    void addCourse(int userID);
    void renameCourse(int userID, int courseID);
    void deleteCourse(int userID, int courseID);

    List<Task> getTasks(int userID, int courseID);
}
