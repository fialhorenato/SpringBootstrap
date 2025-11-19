# Spring Boot(strap) with Kotlin

[![Build](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml/badge.svg)](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/fialhorenato/SpringBootstrap/branch/main/graph/badge.svg?token=8ATZXFJK2Q)](https://codecov.io/gh/fialhorenato/SpringBootstrap)

The project can be seen running here: https://spring-bootstrap.onrender.com/swagger-ui/index.html


## Overview
A starter template for building RESTful APIs with Spring Boot and Kotlin. It includes:
- JWT-based authentication with Spring Security (secured and open endpoints)
- Persistence with Spring Data JPA
- Database migrations with Liquibase
- API documentation via springdoc-openapi (Swagger UI)
- Actuator and Prometheus metrics
- In-memory H2 DB for local development and PostgreSQL profile for local/prod
- Docker image build with a minimized custom JRE (jlink)
- CI with GitHub Actions, Codecov coverage upload, and Snyk scans

## Tech Stack
- Language: Kotlin
- Framework: Spring Boot 3
- Build/Package manager: Gradle (Kotlin DSL) via the Gradle Wrapper
- Web server: Undertow (Tomcat excluded)
- DB: H2 (dev), PostgreSQL (profile), Liquibase migrations
- Auth: JWT (nimbus-jose-jwt)
- Docs: springdoc-openapi
- Metrics: Micrometer + Prometheus
- JDK: 24 (virtual threads enabled)

## Requirements
- JDK 24 (Temurin recommended)
- Docker (optional, for containerized runs)
- Make sure the Gradle Wrapper is executable: `chmod +x ./gradlew`

## Getting Started

### Clone and build
- Clone the repo
- Build and run tests:
  - Unix/macOS: `./gradlew clean build`
  - Windows: `gradlew.bat clean build`

### Run locally (H2, default profile)
- Start the application:
  - Unix/macOS: `./gradlew bootRun`
  - Windows: `gradlew.bat bootRun`
- App port: 8080 (override with `PORT`)
- Actuator port: 8081 (also follows `PORT`; see Environment Variables)
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### Run with local PostgreSQL
- Start a local DB with Docker Compose:
  - `docker compose up -d db`
- Run the app with the PostgreSQL profile:
  - Unix/macOS: `SPRING_PROFILES_ACTIVE=local-postgresql ./gradlew bootRun`
  - Windows (PowerShell): `$env:SPRING_PROFILES_ACTIVE="local-postgresql"; ./gradlew bootRun`

Liquibase will apply migrations automatically on startup.

## Environment Variables
The following variables are read by the application:
- PORT: Server port (defaults to 8080). Note: management.server.port is also configured to use `${PORT:8081}`. Setting PORT affects both due to the same variable name being used. TODO: Clarify intended management port variable.
- JWT_SECRET: Secret for signing JWTs (default present in application.yml for dev only; replace in real deployments).
- SPRING_PROFILES_ACTIVE: Set to `local-postgresql` to use PostgreSQL locally; `render` profile is also available (see application-render.yml).
- For render profile:
  - DATABASE_URL
  - DATABASE_USERNAME
  - DATABASE_PASSWORD

Database defaults (dev):
- H2 in-memory DB `jdbc:h2:mem:db` with username `sa` and password `sa`.

## Common Gradle Tasks and Scripts
- Build: `./gradlew clean build`
- Run app: `./gradlew bootRun`
- Package fat JAR: `./gradlew bootJar` → outputs `build/libs/app.jar`
- Tests: `./gradlew test`
- Coverage report: `./gradlew jacocoTestReport` → `build/reports/coverage/index.html`

## Docker
The Dockerfile builds a minimal runtime image using jdeps + jlink.

Steps:
1) Build the JAR first: `./gradlew clean bootJar`
2) Build the image: `docker build -t springbootstrap:local .`
3) Run the container: `docker run --rm -p 8080:8080 springbootstrap:local`

The CI pipeline also tags images for GHCR as `ghcr.io/fialhorenato/springbootstrap`.

## API Endpoints (samples)
- Hello World (insecure): `GET /hello-world/insecure`
- Hello World (secured, requires ROLE_ADMIN): `GET /hello-world/secure`
- Swagger: `/swagger-ui/index.html`
- Actuator: `/actuator` (management endpoints)

## Testing
- Unit and integration tests use JUnit 5 and Mockito.
- Run: `./gradlew test`
- Coverage: `./gradlew jacocoTestReport`

## Project Structure
- build.gradle.kts — Gradle configuration (Kotlin DSL)
- settings.gradle.kts — Gradle settings
- src/main/kotlin — Application source code
  - com.renato.springbootstrap.SpringBootstrapApplication.kt — entry point
  - com.renato.springbootstrap.api — External API integrations
  - com.renato.springbootstrap.security — Security configuration, JWT handling, authentication services
  - com.renato.springbootstrap.helloworld.controller.HelloWorldController.kt — Sample endpoints
  - com.renato.springbootstrap.exception — Global exception handling
- src/main/resources
  - application.yml — default config (H2, ports, JWT)
  - application-render.yml — Render deployment profile
  - db/changelog — Liquibase changelogs
- src/test/kotlin — Tests
- Dockerfile — multi-stage build with custom JRE
- docker-compose.yml — local Postgres service
- docs/requests/User.http — sample HTTP requests

## CI/CD (GitHub Actions)
Workflow: `.github/workflows/build.yml`
- Builds with JDK 24, runs tests and coverage
- Uploads coverage to Codecov
- Builds and scans Docker image with Snyk
- Pushes images to GitHub Container Registry and prunes old versions

Required repository secrets:
- CODECOV_TOKEN
- SNYK_TOKEN

## Security
See SECURITY.md for security policies and practices.

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0). See the LICENSE file for full terms.
