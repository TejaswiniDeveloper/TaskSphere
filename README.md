# TaskSphere: Employee Task Management REST API

[![CI](https://github.com/TejaswiniDeveloper/TaskSphere/actions/workflows/ci.yml/badge.svg)](https://github.com/TejaswiniDeveloper/TaskSphere/actions/workflows/ci.yml)

A role-based REST API for managing employees and their tasks, built with Spring Boot, Hibernate and MySQL. Admins manage people and assign work. Employees see and update only their own tasks. Authentication uses JWT.

## Features

- JWT login with BCrypt-hashed passwords
- Role-based access control (`ADMIN` and `EMPLOYEE`) using Spring Security method security (`@PreAuthorize`)
- Employee and Task CRUD with a Hibernate One-to-Many relationship
- Ownership check: an employee can only update the status of their own tasks
- Paginated list endpoints (`?page=0&size=10`, size capped at 50)
- Response DTOs: employee responses never include passwords or nested task lists
- Centralized error handling that returns clean JSON for 400, 403, 404 and 409 errors, without leaking internal details
- Input validation with Jakarta Bean Validation
- Swagger / OpenAPI documentation with JWT bearer authentication
- Unit tests for the service layer with JUnit 5 and Mockito (no database needed)
- Multi-stage Dockerfile and a GitHub Actions pipeline that runs the unit tests and builds the Docker image on every push

## Tech Stack

Java 17, Spring Boot 4.1.1, Spring Security, Spring Data JPA (Hibernate), MySQL 8, JWT (jjwt 0.13.0), springdoc-openapi (Swagger UI), JUnit 5, Mockito, Maven, Lombok

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
| GET | `/api/employees?page=0&size=10` | Logged in. Paginated |
| GET | `/api/employees/{id}` | Logged in |
| POST | `/api/employees` | ADMIN |
| PUT | `/api/employees/{id}` | ADMIN |
| DELETE | `/api/employees/{id}` | ADMIN |
| GET | `/api/tasks?page=0&size=10` | ADMIN. Paginated |
| GET | `/api/tasks/my` | Logged in. Returns only your tasks |
| PATCH | `/api/tasks/{id}/status` | Assignee only. Body: `{"status": "IN_PROGRESS"}` |
| POST | `/api/tasks` | ADMIN |
| GET | `/api/tasks/{id}`, `/api/tasks/employee/{employeeId}` | ADMIN |
| PUT | `/api/tasks/{id}` | ADMIN |
| DELETE | `/api/tasks/{id}` | ADMIN |

Task status values: `PENDING`, `IN_PROGRESS`, `COMPLETED`.

## API Documentation (Swagger)

Start the app and open **http://localhost:8080/swagger-ui.html** to browse every endpoint and try it from the browser.

To call a protected endpoint:

1. Open `POST /api/auth/login`, click **Try it out**, enter an email and password, and click **Execute**.
2. Copy the `token` value from the response.
3. Click **Authorize** at the top of the page, paste the token, and confirm.
4. Try any endpoint. Swagger sends the token with every request.

The raw OpenAPI definition is available at `/v3/api-docs`.

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

## Running the Tests

The service-layer unit tests use Mockito and need no database:

```
.\mvnw.cmd test -Dtest="EmployeeServiceTest,TaskServiceTest"
```

Running plain `mvnw test` also runs the application-context test, which needs MySQL running.

## Project Structure

```
com.example.taskmanager
  config/       Security and OpenAPI configuration
  controller/   REST controllers
  dto/          Request and response objects
  entity/       JPA entities and enums
  exception/    Custom exception and global handler
  repository/   Spring Data JPA repositories
  security/     JWT utility, JWT filter, user details service
  service/      Business logic
```

## Roadmap

Planned, not built yet:

- Response DTOs for tasks
- Integration tests for controllers and repositories
- Split into microservices (Employee Service, Task Service) with an API gateway and service discovery
- Deployment on AWS (RDS, EC2, S3, SES)

## Author

Tejaswini ([@TejaswiniDeveloper](https://github.com/TejaswiniDeveloper))