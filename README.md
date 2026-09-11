# Leave Management System

## Overview

A production-style Spring Boot REST API for managing employee leave requests. It supports the full lifecycle of a leave request — **create, retrieve, update, delete, and track status** — backed by a clean, layered architecture (Controller → Service → Repository) with DTO-based contracts, centralized validation, and global exception handling.

## Tech Stack

- Java 17
- Spring Boot 3.2.11 (Web, Validation)
- Gradle (build tool — this project does not use Maven/`pom.xml`)
- Lombok
- JUnit 5, Mockito, MockMvc

## Project Structure

```
src/main/java/com/example/leavemanagement/
├── controller/       # REST endpoints (LeaveController) — DTOs in/out only
├── service/          # Business logic (LeaveService, LeaveServiceImpl)
├── mapper/           # Entity <-> DTO conversion (LeaveMapper)
├── repository/       # Data access (in-memory implementation)
├── model/            # Domain entities & enums (LeaveRequest, LeaveType, LeaveStatus)
├── dto/               # Request/response payloads (LeaveCreateDTO, LeaveResponseDTO)
└── exception/         # Custom exceptions & GlobalExceptionHandler
```

## Prerequisites

- **Java JDK 17** or later (`java -version` to confirm)
- No local Gradle install required — the project ships the Gradle Wrapper (`./gradlew`), which provisions Gradle 9.7.1 automatically
- Git

## Setup, Build & Run

Clone the repository, then from the project root:

```bash
# Build the project and run all tests (equivalent to `mvn clean install`)
./gradlew clean build

# Run the application (equivalent to `mvn spring-boot:run`)
./gradlew bootRun
```

The API starts on **http://localhost:8081** (dev profile).

> Note: this project is Gradle-based (`build.gradle`, `gradlew`), not Maven-based, so the equivalent `./gradlew` commands above are used instead of `mvn`.

## Running Tests

```bash
# Equivalent to `mvn test`
./gradlew test
```

Test reports are generated at `build/reports/tests/test/index.html`.

## API Endpoints

Base path: `/leaves`

| Method | Endpoint       | Request Body                                                                 | Success Status |
|--------|----------------|-------------------------------------------------------------------------------|-----------------|
| POST   | `/leaves`      | `LeaveCreateDTO` (employeeId, leaveType, startDate, endDate, reason)          | 201 Created     |
| GET    | `/leaves`      | —                                                                              | 200 OK          |
| GET    | `/leaves/{id}` | —                                                                              | 200 OK          |
| PUT    | `/leaves/{id}` | `LeaveCreateDTO` (employeeId, leaveType, startDate, endDate, reason)          | 200 OK          |
| DELETE | `/leaves/{id}` | —                                                                              | 204 No Content  |



### Sample Request — Create Leave

```http
POST /leaves
Content-Type: application/json

{
  "employeeId": 101,
  "leaveType": "SICK",
  "startDate": "2026-09-15",
  "endDate": "2026-09-17",
  "reason": "Fever and flu"
}
```

### Sample Response

```json
{
  "id": 1,
  "employeeId": 101,
  "leaveType": "SICK",
  "startDate": "2026-09-15",
  "endDate": "2026-09-17",
  "reason": "Fever and flu",
  "status": "PENDING"
}
```

### Enums

- **LeaveType**: `SICK`, `CASUAL`, `ANNUAL`
- **LeaveStatus**: `PENDING`, `APPROVED`, `REJECTED`

## Architecture & Best Practices

- **Strict layer isolation**: Controllers only accept/return DTOs (`LeaveCreateDTO`, `LeaveResponseDTO`) — domain entities never cross the controller boundary.
- **Dedicated mapper**: Entity ⇄ DTO conversion lives in `LeaveMapper`, not in the controller or scattered across the service.
- **Constructor injection**: All dependencies (`LeaveRepository`, `LeaveMapper`) are injected via constructors (`@RequiredArgsConstructor`), never field-based `@Autowired`.
- **Centralized exception handling**: `@RestControllerAdvice` handles validation errors (400), not-found errors (404), malformed JSON (400), and unexpected errors (500) — controllers stay free of try/catch blocks.

## Validation & Error Handling

Requests are validated using Bean Validation (`employeeId`, `leaveType`, `startDate`, `endDate`, `reason` are required; `startDate` must be present/future). Errors return a consistent JSON shape:

```json
{
  "status": 400,
  "message": "Validation Failed",
  "timestamp": "2026-09-10T12:00:00",
  "errors": {
    "employeeId": "Employee ID is required",
    "reason": "Reason cannot be empty"
  }
}
```

Handled cases: validation errors (400), resource not found (404), malformed JSON (400), invalid date range (400), and unexpected errors (500).

## Live API Demo

All endpoints were verified end-to-end against the running app (`./gradlew bootRun`, port 8081). Full request/response output is captured in [`screenshots/api-demo-output.txt`](screenshots/api-demo-output.txt), covering:

1. `GET /leaves` on an empty store → `200 [] `
2. `POST /leaves` with valid data → `201 Created`
3. `POST /leaves` with invalid data → `400` with field-level validation errors
4. `GET /leaves/{id}` for an existing record → `200 OK`
5. `GET /leaves/{id}` for a missing record → `404 Not Found`
6. `PUT /leaves/{id}` to update a record → `200 OK`
7. `POST /leaves` with an invalid date range (end before start) → `400 Bad Request`
8. `PUT /leaves/{id}` for a missing record → `404 Not Found`
9. `POST /leaves` with a malformed JSON body → `400 Bad Request`
10. `GET /leaves` listing the updated record → `200 OK`
11. `DELETE /leaves/{id}` → `204 No Content`
12. `GET /leaves/{id}` after delete → `404 Not Found`

Test run summary is captured in [`screenshots/test-results-summary.txt`](screenshots/test-results-summary.txt) — 12/12 tests passing.

## Testing Coverage

- **Unit tests** (`LeaveServiceTest`) — success and failure paths using Mockito, including negative cases: `getLeaveById`, `updateLeave`, and `deleteLeave` all throw `ResourceNotFoundException` for non-existent IDs.
- **Controller tests** (`LeaveControllerTest`) — HTTP layer using `@WebMvcTest` and `MockMvc`, verifying status codes and JSON responses, including `400 Bad Request` on invalid `POST`/`PUT` payloads and `404 Not Found` for missing resources.

## License

This project is for educational/assignment purposes.

## Development Workflow Note

As this is a personal assignment repository with no collaborators, the initial implementation
(project setup, models/DTOs, exceptions, repository, service, controller, initial tests) was
committed directly to `main`. The subsequent refactor (LeaveMapper extraction, configuration/test
updates, documentation) was developed on the `feature/leave-mapper-refactor` branch and submitted
via Pull Request, per the assignment's branch/PR requirement.
