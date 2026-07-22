package com.javaproject.java_project.repository;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTaskIDAndCourse(Long taskID, Course course);
}
