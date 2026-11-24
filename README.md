# Dynamic RBAC Authorization Engine

## 1. Project Overview

The Dynamic RBAC Authorization Engine is a Spring Boot backend that manages users, roles, permissions, and role-permission assignments through a database. Authorization decisions are evaluated dynamically at runtime instead of relying on hardcoded role checks.

Technology stack:

- Java 21
- Spring Boot 3.5.7
- Spring Security
- Spring Data JPA
- H2 Database
- Flyway
- JJWT 0.12.6
- Maven
- Lombok
- Jakarta Validation
- JUnit 5, Spring Test, and Mockito
- JaCoCo

## 2. Features

- Database-driven users, roles, permissions, and mappings
- JWT login and bearer-token authentication
- Method-level security with `@EnableMethodSecurity`
- Custom Spring Security `PermissionEvaluator`
- Runtime permission checks through `UserRole -> Role -> RolePermission -> Permission`
- BCrypt password hashing
- H2 persistence with Flyway migrations
- Centralized validation and exception handling
- Unit and integration-style tests for login, APIs, JWT, permission evaluation, and authorization

## 3. Database Design

The JPA entities are:

| Entity | Important fields |
|---|---|
| `User` | `id`, `username`, `password`, `active` |
| `Role` | `id`, `name` |
| `Permission` | `id`, `name` |
| `UserRole` | `id`, `user_id`, `role_id` |
| `RolePermission` | `id`, `role_id`, `permission_id` |

`UserRole` assigns roles to users. `RolePermission` assigns permissions to roles. Unique constraints prevent duplicate user-role and role-permission mappings.

H2 stores the database persistently at `jdbc:h2:file:./data/rbacdb;DB_CLOSE_ON_EXIT=FALSE`. Flyway migrations create the five tables and seed initial roles, permissions, and mappings. Hibernate uses `ddl-auto: validate`, so it validates but does not create or alter the Flyway-managed schema.

## 4. Authorization Flow

1. `POST /auth/login` receives the username and password.
2. `AuthServiceImpl` loads the active user and verifies the BCrypt password.
3. `JwtService` creates a signed JWT containing the username as the subject.
4. The client sends the token as `Authorization: Bearer <token>`.
5. `JwtAuthenticationFilter` verifies the token and loads the active user.
6. A controller method is protected with `@PreAuthorize` and a permission expression.
7. Spring Security calls `DynamicPermissionEvaluator`.
8. `RolePermissionRepository` checks the database through `UserRole -> Role -> RolePermission -> Permission`.
9. The request is granted or denied based on the current database mapping.

Authentication proves the user's identity. Authorization determines whether the user has the requested database permission.

## 5. Permission Evaluation and Design Decisions

The project uses expressions such as:

```java
@PreAuthorize("hasPermission(null, 'MANAGE_ROLES')")
```

Other permission expressions include `MANAGE_PERMISSIONS`, `MANAGE_USERS`, `ASSIGN_PERMISSIONS`, `ASSIGN_ROLES`, and `READ_SECURE_DATA`.

`DynamicPermissionEvaluator` implements both `PermissionEvaluator` methods. It rejects missing or anonymous authentication, obtains the current username, and asks `RolePermissionRepository.hasPermission(username, permissionName)` for the database result.

Hardcoded checks such as `hasRole('ADMIN')` or `hasAuthority('MANAGE_USERS')` are avoided. Database permissions can change by updating role-permission mappings without changing Java authorization code or rebuilding the application.

Authorization logic is centralized in `DynamicPermissionEvaluator`, making all protected methods reuse the same database permission evaluation behavior.

## 6. API Endpoints

| Method | Endpoint | Required authentication and permission |
|---|---|---|
| `POST` | `/auth/login` | Public login endpoint |
| `POST` | `/roles` | Authenticated; `MANAGE_ROLES` |
| `POST` | `/permissions` | Authenticated; `MANAGE_PERMISSIONS` |
| `POST` | `/users` | Authenticated; `MANAGE_USERS` |
| `POST` | `/roles/{roleId}/permissions/{permissionId}` | Authenticated; `ASSIGN_PERMISSIONS` |
| `POST` | `/users/{userId}/roles/{roleId}` | Authenticated; `ASSIGN_ROLES` |
| `GET` | `/secure-data` | Authenticated; `READ_SECURE_DATA` |

Common response codes:

- `200 OK`: login or successful secure-data retrieval
- `201 Created`: role, permission, user, or mapping successfully created
- `400 Bad Request`: validation or invalid ID error
- `401 Unauthorized`: missing or invalid authentication
- `403 Forbidden`: authenticated user lacks the required permission
- `404 Not Found`: referenced role, permission, or user does not exist
- `409 Conflict`: duplicate record or mapping

## 7. Setup and Run Instructions

Prerequisites:

- JDK 21
- Maven
- Git

Clone and enter the project:

```powershell
git clone https://github.com/Ajinkya-Dhavale/dynamic-rbac-authorization-engine.git
cd dynamic-rbac-authorization-engine
```

Configure the JWT secret locally. Do not commit the secret:

```powershell
$env:JWT_SECRET = "<random-secret-with-at-least-32-characters>"
```

The application can also load local environment variables from `.env` through `spring-dotenv`, when configured locally. `.env` must not be committed.

Start the application:

```powershell
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

Verify startup by checking for successful Flyway migration and logging in through `/auth/login`.

## 8. Example API Request Flow

Successful login:

```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "<password>"
}
```

Use the returned `accessToken`:

```http
GET /secure-data
Authorization: Bearer <jwt>
```

Authorized access returns `200 OK` with a secure-data response.

Unsuccessful authorization example:

```http
GET /secure-data
Authorization: Bearer <jwt-without-read-secure-data>
```

The response is `403 Forbidden`. Without an Authorization header or with an invalid JWT, the response is `401 Unauthorized`.

## 9. Testing

Run the tests:

```powershell
mvn clean test
```

Run tests and generate JaCoCo verification:

```powershell
mvn clean verify
```

The JaCoCo plugin generates the report at:

```text
target/site/jacoco/index.html
```

JaCoCo is configured to enforce at least 80% instruction coverage. A current report may not exist until `mvn clean verify` is run. A previously reported verification run showed 69 tests with zero failures or errors, 93% instruction coverage, and 76% branch coverage.

## 10. Assumptions and Design Decisions

- H2 file storage is used for persistent assignment testing and local development.
- Flyway, rather than Hibernate, creates and seeds the database schema.
- H2 and file persistence are suitable for this assignment, but a separate production database would be required for production deployment.
- `JWT_SECRET` is environment-configurable and must contain at least 32 characters.
- Real passwords, password hashes, JWT values, and environment secrets are not committed or documented in this README.
