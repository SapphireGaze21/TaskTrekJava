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

## Run the application

### Prerequisites

- Java JDK 21 or later (`java -version`)
- Node.js 20 or later (`node --version`)
- PostgreSQL running locally on port `5432`
- PostgreSQL's `psql` command available on `PATH` (installed with PostgreSQL)
- Internet access on the first run, so Maven and npm can download dependencies

### Start everything

From the project root, run the launcher for your operating system:

**Windows Command Prompt**

```cmd
scripts\start_app.cmd
```

**macOS or Linux terminal**

```bash
bash scripts/start-app.sh
```

On first launch, the script:

1. Prompts for your local PostgreSQL username and password. These are used only by the current launch and are not saved.
2. Creates the `tasktrek_db` database if it does not already exist. Your PostgreSQL user needs permission to create databases the first time.
3. Installs frontend packages when `frontend/node_modules` is missing.
4. Starts the Spring Boot API and Vite frontend in the background.

The script prints the chosen URLs when both services are ready. It prefers API port `8080` and frontend port `5173`; if either is busy, it automatically chooses the next available port. Open the printed frontend URL in a browser.

To provide credentials without a prompt for the current shell session:

**Windows Command Prompt**

```cmd
set TASKTREK_DB_USERNAME=postgres
set TASKTREK_DB_PASSWORD=your_postgres_password
scripts\start_app.cmd
```

**macOS or Linux**

```bash
export TASKTREK_DB_USERNAME=postgres
export TASKTREK_DB_PASSWORD=your_postgres_password
bash scripts/start-app.sh
```

To use a database name other than `tasktrek_db`:

```cmd
scripts\start_app.cmd -DatabaseName your_database_name
```

```bash
bash scripts/start-app.sh --database-name your_database_name
```

### Stop everything

The frontend and API run as background processes, so closing the browser does not stop them. Run the matching stop script:

**Windows Command Prompt**

```cmd
scripts\stop_app.cmd
```

**macOS or Linux terminal**

```bash
bash scripts/stop-app.sh
```

This stops only the API and frontend processes recorded by the TaskTrek launcher and releases their ports.

### Troubleshooting

- Startup logs are in [`.runtime`](.runtime) at the project root. Check the newest `launcher-*.err.log` first, then `api-*.out.log` or `frontend-*.out.log`.
- If PostgreSQL authentication fails, verify the username/password entered at startup can connect to your local PostgreSQL server.
- If database creation fails, create it manually in `psql` with `CREATE DATABASE tasktrek_db;`, or grant your PostgreSQL user `CREATEDB` permission.
- If Maven cannot download dependencies, check firewall, proxy, VPN, or Maven Central network access.

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

The launcher supplies database credentials through environment variables. They override the defaults in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/${TASKTREK_DB_NAME:tasktrek_db}
spring.datasource.username=${TASKTREK_DB_USERNAME:}
spring.datasource.password=${TASKTREK_DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
```

For direct Maven runs, set `TASKTREK_DB_USERNAME`, `TASKTREK_DB_PASSWORD`, and optionally `TASKTREK_DB_NAME` in your terminal before running `mvnw.cmd spring-boot:run`.
