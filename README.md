### TaskTrek - A Gamified Task Tracker (Java Spring Boot)

## Overview

A productivity tracker for students inspired by Google Tasks & Duolingo, with course and task management, gamified with XP and levels.
Built with Spring Boot in Java.

The current frontend-ready REST contract is documented in [API.md](API.md). All current routes are versioned under `/api` (for example, `/api/auth/login` and `/api/courses`).

Users can:
- Register & Log in (via `/auth`)
- Receive stateless JWT tokens upon successful login
- Create and manage courses (via `/courses`)
- Add and complete tasks (via `/courses/{courseId}/tasks`), earning XP and leveling up

## Tech Stack

- **Language**: Java (JDK 21+)
- **Framework**: Spring Boot 4.0.0
- **Security**: Spring Security & JSON Web Tokens (JWT)
- **Database**: PostgreSQL (JPA / Hibernate)
- **Tools**: Maven, Postman

---

## Project Structure

```text
src/main/java/com/javaproject/java_project/
├── config/
│   └── SecurityConfig.java            # Spring Security and AuthenticationProvider config
├── controller/
│   ├── AuthController.java            # Handles registration & authentication
│   ├── CourseController.java          # Handles Course CRUD
│   └── TaskController.java            # Handles Task CRUD & completion
├── model/
│   ├── User.java                      # App User Entity
│   ├── Course.java                    # Course Entity
│   ├── Task.java                      # Base Task Entity (Single-table inheritance)
│   ├── AssignmentTask.java            # Specific Task subclass
│   ├── ExamPrepTask.java              # Specific Task subclass
│   ├── ProjectTask.java               # Specific Task subclass
│   ├── QuizPrepTask.java              # Specific Task subclass
│   ├── Achievement.java               # Achievements tracking Entity
│   └── SkillProgress.java             # Course-specific XP tracking Entity
├── repository/
│   ├── UserRepository.java            # DB access for Users
│   ├── CourseRepository.java          # DB access for Courses
│   ├── TaskRepository.java            # DB access for Tasks
│   └── SkillProgressRepository.java   # DB access for SkillProgress
├── request/
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   ├── NewCourseRequest.java
│   ├── NewTaskRequest.java
│   └── EditedTaskRequest.java
├── security/
│   ├── CustomUserDetailsService.java  # Spring Security UserDetails loader
│   ├── JwtFilter.java                 # Filters requests for Authorization JWT tokens
│   └── JwtService.java                # Token generation & validation utility
└── service/
    ├── AuthService.java               # Authentication logic
    ├── CourseService.java             # Course CRUD service
    ├── TaskService.java               # Task CRUD & XP completion service
    └── SkillProgressService.java      # Level & XP progress service
```

---

## Features

- **Stateless JWT Auth**: All endpoints (except `/auth/login` and `/auth/register`) are fully secured with token validation.
- **Relational Storage**: Relational structure backed by a PostgreSQL database utilizing Spring Data JPA.
- **BCrypt Encryption**: Passwords hashed securely during registration.
- **Course & Task CRUD**: Standard operations mapping courses and different sub-task types (Assignments, Exams, Projects, Quizzes) to the database.
- **Gamified Actions**: Earn variable XP depending on task difficulty. Day streaks are tracked to increase XP gain.

---

## API Endpoints Summary

All routes (except `/auth/**`) require the header: `Authorization: Bearer <JWT_token>`

| Method | Endpoint | Description |
| :---: | :--- | :--- |
| **POST** | `/auth/register` | Register a new user with BCrypt password hashing |
| **POST** | `/auth/login` | Login and receive a JWT token |
| **GET** | `/courses` | Show all courses of the logged-in user |
| **POST** | `/courses` | Create a new course under the user |
| **PUT** | `/courses/{id}` | Rename a course of ID in path |
| **DELETE** | `/courses/{id}` | Delete a course of ID in path |
| **GET** | `/courses/{id}` | Shows all tasks of a course |
| **POST** | `/courses/{id}/tasks` | Create a task under a course |
| **PATCH** | `/courses/{courseId}/tasks/{taskId}` | Edit details of a task |
| **DELETE** | `/courses/{courseId}/tasks/{taskId}` | Delete a task |
| **POST** | `/courses/{courseId}/tasks/{taskId}/complete` | Mark a task as complete, calculate streak bonuses, and award XP |

---

## Database Configuration

The application is configured to connect to PostgreSQL. Update your configuration in `src/main/resources/application.properties` to match your local setup:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasktrek_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update
```
