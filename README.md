# Dynamic RBAC Authorization Engine

A Spring Boot application that implements dynamic Role-Based Access Control (RBAC), allowing access to resources based on user permissions stored in a database.

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- H2 Database
- Maven

## Features

- Dynamic role and permission management
- Assign permissions to roles
- Assign roles to users
- Runtime permission evaluation
- Method-level security using `@PreAuthorize`
- Custom Spring Security `PermissionEvaluator`
- Database-driven authorization

*Features will be documented as they are implemented and tested.*

## Prerequisites

- JDK 21
- Git

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Ajinkya-Dhavale/dynamic-rbac-authorization-engine.git
cd dynamic-rbac-authorization-engine
```

### 2. Build the project

Windows:

```powershell
.\mvnw.cmd clean package
```

Linux/macOS:

```bash
./mvnw clean package
```

### 3. Run the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The application runs on `http://localhost:8080` by default.

## Testing

Run the automated tests:

```bash
.\mvnw.cmd test
```

Generate the code coverage report:

```bash
.\mvnw.cmd verify
```

## Assignment

Developed as part of the Dynamic RBAC Authorization Engine assignment for TechPulse IT Services Pvt. Ltd.
