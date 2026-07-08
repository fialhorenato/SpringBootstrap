# CLAUDE.md

### Project Overview
Production-ready Spring Boot + Kotlin starter template for building RESTful APIs. Uses Spring Boot 4.0.3, JDK 24 (Virtual Threads), Gradle (Kotlin DSL). Supports PostgreSQL and H2.

### Architecture Summary

#### Core Components
*   **API Layer:** Controllers handle business logic; DTOs are in `api/request/` and `api/response/`. Global exception handling is in `exception/`. Swagger UI at `/swagger-ui/index.html`.
*   **Security:** Handled via JWT authentication (located in the `security/` package).
    *   `config/SecurityConfig.kt`: Spring Security setup with JWT filter.
    *   `utils/JwtUtils.kt`: Token generation/validation.
*   **Database:** Liquibase migrations are stored in `src/main/resources/db/changelog/`. JPA repositories are in `security/repository/`. H2 is for development; PostgreSQL is for production.

#### Configuration & Environments
*   **Profile Management:** `application.yml` (Default profile: H2). Use `application-render.yml` for Production profiles, managing environment variables like `JWT_SECRET`, `DATABASE_URL`, etc.
*   **Dependencies:** Key dependencies are defined in `build.gradle.kts`, including Jackson BOMs and PostgreSQL (`runtimeOnly("org.postgresql:postgresql")`).

### Common Build Commands

**Build & Test:**
```bash
# Clean, build, and run tests
./gradlew clean build

# Run locally (H2 database)
./gradlew bootRun

# Run with PostgreSQL
docker compose up -d db
SPRING_PROFILES_ACTIVE=local-postgresql ./gradlew bootRun

# Generate coverage report
./gradlew jacocoTestReport
```

### Development Notes
*   JDK 24 is required.
*   Actuator runs on port 8081; main application on port 8080.
*   Client endpoints can be tested using sample HTTP requests (e.g., `docs/requests/User.http`).
