package com.javaproject.java_project.repository;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = "tasks")
    List<Course> findByUser(User user);

    @EntityGraph(attributePaths = "tasks")
    Optional<Course> findByCourseIdAndUser(Long courseId, User user);
}
