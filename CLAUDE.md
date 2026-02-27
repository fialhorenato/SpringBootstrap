# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Production-ready Spring Boot + Kotlin starter template for building RESTful APIs. Uses Spring Boot 4.0.3, JDK 24 with virtual threads, Gradle (Kotlin DSL), and PostgreSQL with H2 for development.

## Common Commands

```bash
# Build and test
./gradlew clean build

# Run locally (H2 database)
./gradlew bootRun

# Run with PostgreSQL
docker compose up -d db
SPRING_PROFILES_ACTIVE=local-postgresql ./gradlew bootRun

# Run single test
./gradlew test --tests "FullyQualifiedTestClassName"

# Generate coverage report
./gradlew jacocoTestReport

# Package JAR
./gradlew bootJar
```

## Architecture

### Security Module
The `security/` package contains JWT authentication:
- `config/SecurityConfig.kt` - Spring Security configuration with JWT filter
- `utils/JwtUtils.kt` - JWT token generation/validation
- `filter/JwtAuthorizationFilter.kt` - Request authentication filter
- `service/` - User and authentication business logic
- `entity/` - UserEntity, RoleEntity (JPA)

### Database
- Liquibase migrations in `src/main/resources/db/changelog/`
- JPA repositories in `security/repository/`
- H2 for development, PostgreSQL for production

### API Layer
- Controllers return DTOs from `api/request/` and `api/response/`
- Global exception handling in `exception/`
- Swagger UI available at `/swagger-ui/index.html`

### Configuration
- `application.yml` - Default profile (H2, dev JWT secret)
- `application-render.yml` - Production profile for Render.com
- Environment variables: `PORT`, `JWT_SECRET`, `SPRING_PROFILES_ACTIVE`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`

### Testing
- JUnit 5 with Mockito and AssertJ
- Test factories: `UserFactory.kt`, `RoleFactory.kt`
- Security tests in `src/test/kotlin/com/renato/springbootstrap/security/`

### Docker
- Multi-stage Dockerfile uses jlink to create minimal JRE
- Pre-built images: `ghcr.io/fialhorenato/springbootstrap:latest`

## Development Notes

- JDK 24 is required (set in `system.properties`)
- Gradle configuration cache is enabled (`gradle.properties`)
- Actuator runs on port 8081, main app on port 8080
- Sample HTTP requests in `docs/requests/User.http`
