package com.javaproject.java_project.repository;

import com.javaproject.java_project.model.SkillProgress;
import com.javaproject.java_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillProgressRepository extends JpaRepository<SkillProgress, Long> {
    Optional<SkillProgress> findByUserAndCourseName(User user, String courseName);
}
