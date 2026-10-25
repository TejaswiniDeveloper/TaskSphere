# TaskSphere: Employee Task Management REST API

A role-based REST API for managing employees and their tasks, built with Spring Boot, Hibernate and MySQL. Admins manage people and assign work. Employees see and update only their own tasks. Authentication uses JWT.

## Features

- JWT login with BCrypt-hashed passwords
- Role-based access control (`ADMIN` and `EMPLOYEE`) using Spring Security method security (`@PreAuthorize`)
- Employee and Task CRUD with a Hibernate One-to-Many relationship
- Ownership check: an employee can only update the status of their own tasks
- Centralized error handling that returns clean JSON for 404, 403 and validation errors
- Input validation with Jakarta Bean Validation
- Passwords are never returned in API responses

## Tech Stack

Java 17, Spring Boot 4.1.1, Spring Security, Spring Data JPA (Hibernate), MySQL 8, JWT (jjwt 0.13.0), Maven, Lombok

## Access Rules

| Action | Anonymous | EMPLOYEE | ADMIN |
|---|---|---|---|
| Register, login | yes | yes | yes |
| View employees | no | yes | yes |
| Create, update, delete employee | no | no | yes |
| View all tasks, create, update, delete task | no | no | yes |
| View my tasks | no | yes | yes |
| Update status of a task assigned to me | no | yes | yes |

## API Endpoints

Send the token as `Authorization: Bearer <token>`.

| Method | Path | Access |
|---|---|---|
| POST | `/api/auth/register` | Public. Always creates an `EMPLOYEE` |
| POST | `/api/auth/login` | Public. Returns a JWT |
| GET | `/api/employees` | Logged in |
| GET | `/api/employees/{id}` | Logged in |
| POST | `/api/employees` | ADMIN |
| PUT | `/api/employees/{id}` | ADMIN |
| DELETE | `/api/employees/{id}` | ADMIN |
| GET | `/api/tasks/my` | Logged in. Returns only your tasks |
| PATCH | `/api/tasks/{id}/status` | Assignee only. Body: `{"status": "IN_PROGRESS"}` |
| POST | `/api/tasks` | ADMIN |
| GET | `/api/tasks`, `/api/tasks/{id}`, `/api/tasks/employee/{employeeId}` | ADMIN |
| PUT | `/api/tasks/{id}` | ADMIN |
| DELETE | `/api/tasks/{id}` | ADMIN |

Task status values: `PENDING`, `IN_PROGRESS`, `COMPLETED`.

## Getting Started

**Prerequisites:** JDK 17 and MySQL 8.

1. Create the database:
   ```sql
   CREATE DATABASE taskmanager_db;
   ```
2. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`. Fill in your MySQL username and password, and set `jwt.secret` to a random string of at least 32 characters. This file is git-ignored, so your secrets stay on your machine.
3. Run the app (Windows: `.\mvnw.cmd spring-boot:run`, macOS or Linux: `./mvnw spring-boot:run`). Hibernate creates the tables automatically. The API runs on `http://localhost:8080`.

### Creating the first admin

Registration always creates an `EMPLOYEE`, and creating employees needs an admin, so the first admin is promoted by hand:

1. Register a user with `POST /api/auth/register`.
2. Promote that user in MySQL:
   ```sql
   UPDATE employees SET role = 'ADMIN' WHERE email = 'you@example.com';
   ```
3. Log in with `POST /api/auth/login` to get a token.

## Project Structure

```
com.example.taskmanager
  config/       Security configuration
  controller/   REST controllers
  dto/          Request objects
  entity/       JPA entities and enums
  exception/    Custom exception and global handler
  repository/   Spring Data JPA repositories
  security/     JWT utility, JWT filter, user details service
  service/      Business logic
```

## Roadmap

Planned, not built yet:

- DTO layer for responses
- Unit and integration tests
- Swagger / OpenAPI documentation
- Split into microservices (Employee Service, Task Service) with an API gateway and service discovery
- Docker and deployment on AWS (RDS, EC2, S3, SES)

## Author

Tejaswini ([@TejaswiniDeveloper](https://github.com/TejaswiniDeveloper))