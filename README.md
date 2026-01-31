# Spring Boot(strap) with Kotlin

[![Build](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml/badge.svg)](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/fialhorenato/SpringBootstrap/branch/main/graph/badge.svg?token=8ATZXFJK2Q)](https://codecov.io/gh/fialhorenato/SpringBootstrap)

A production-ready starter template for building RESTful APIs with Spring Boot and Kotlin.

**Live Demo:** https://spring-bootstrap.onrender.com/swagger-ui/index.html


## Features

### Core Capabilities
- **Authentication & Authorization**: JWT-based authentication with Spring Security, supporting both secured and open endpoints
- **Database**: Spring Data JPA with Liquibase migrations
  - H2 in-memory database for local development
  - PostgreSQL for production environments
- **API Documentation**: Interactive Swagger UI via springdoc-openapi
- **Monitoring**: Actuator endpoints with Prometheus metrics integration
- **Containerization**: Optimized Docker image using jlink for minimal JRE footprint

### Development & CI/CD
- **Build System**: Gradle with Kotlin DSL
- **Testing**: Comprehensive test coverage with JUnit 5 and Mockito
- **Continuous Integration**: GitHub Actions with automated testing, coverage reporting (Codecov), and security scanning (Snyk)
- **Container Registry**: Automated image publishing to GitHub Container Registry

## Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin |
| **Framework** | Spring Boot 4 |
| **JDK** | 24 (virtual threads enabled) |
| **Build Tool** | Gradle (Kotlin DSL) via Gradle Wrapper |
| **Web Server** | Tomcat (embedded) |
| **Database** | H2 (dev), PostgreSQL (production) |
| **Migrations** | Liquibase |
| **Authentication** | JWT (nimbus-jose-jwt) |
| **API Documentation** | springdoc-openapi |
| **Metrics** | Micrometer + Prometheus |

## Prerequisites

- **JDK 24** (Eclipse Temurin recommended)
- **Docker** (optional, for containerized development and deployment)
- **Git** (for cloning the repository)

> **Note**: On Unix/macOS, ensure the Gradle Wrapper is executable: `chmod +x ./gradlew`

## Getting Started

### 1. Clone and Build

```bash
# Clone the repository
git clone https://github.com/fialhorenato/SpringBootstrap.git
cd SpringBootstrap

# Build and run tests
./gradlew clean build          # Unix/macOS
gradlew.bat clean build        # Windows
```

### 2. Run Locally (H2 Database)

```bash
# Start the application
./gradlew bootRun              # Unix/macOS
gradlew.bat bootRun            # Windows
```

**Access Points:**
- API Server: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Actuator: http://localhost:8081/actuator

> **Note**: Default ports are 8080 (application) and 8081 (management). Override with `PORT` environment variable.

### 3. Run with PostgreSQL (Optional)

For a production-like setup with PostgreSQL:

```bash
# Start PostgreSQL with Docker Compose
docker compose up -d db

# Run the application with PostgreSQL profile
SPRING_PROFILES_ACTIVE=local-postgresql ./gradlew bootRun                    # Unix/macOS
$env:SPRING_PROFILES_ACTIVE="local-postgresql"; ./gradlew bootRun            # Windows PowerShell
```

Liquibase automatically applies database migrations on startup.

## Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `PORT` | Application server port | `8080` | No |
| `JWT_SECRET` | Secret key for signing JWTs | *(dev default)* | **Yes** (production) |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` | No |
| `DATABASE_URL` | Database connection URL (render profile) | - | Yes (render) |
| `DATABASE_USERNAME` | Database username (render profile) | - | Yes (render) |
| `DATABASE_PASSWORD` | Database password (render profile) | - | Yes (render) |

### Profiles

- **default**: H2 in-memory database (`jdbc:h2:mem:db`, user: `sa`, password: `sa`)
- **local-postgresql**: Local PostgreSQL database
- **render**: Production deployment on Render.com

> **Security Warning**: The default `JWT_SECRET` in `application.yml` is for development only. Always use a strong, unique secret in production environments.

## Development

### Common Gradle Tasks

| Task | Command | Output |
|------|---------|--------|
| **Build project** | `./gradlew clean build` | - |
| **Run application** | `./gradlew bootRun` | - |
| **Package JAR** | `./gradlew bootJar` | `build/libs/app.jar` |
| **Run tests** | `./gradlew test` | - |
| **Generate coverage report** | `./gradlew jacocoTestReport` | `build/reports/coverage/index.html` |

### Testing

Tests use JUnit 5 and Mockito for unit and integration testing.

```bash
# Run all tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# View coverage report
open build/reports/coverage/index.html    # macOS
xdg-open build/reports/coverage/index.html # Linux
```

## Docker

The project includes an optimized multi-stage Dockerfile that creates a minimal runtime image using `jdeps` and `jlink` to generate a custom JRE.

### Build and Run with Docker

```bash
# 1. Build the application JAR
./gradlew clean bootJar

# 2. Build the Docker image
docker build -t springbootstrap:local .

# 3. Run the container
docker run --rm -p 8080:8080 springbootstrap:local
```

### Pre-built Images

Pre-built images are available on GitHub Container Registry:

```bash
docker pull ghcr.io/fialhorenato/springbootstrap:latest
```

## API Reference

### Sample Endpoints

| Endpoint | Method | Authentication | Description |
|----------|--------|----------------|-------------|
| `/hello-world/insecure` | GET | None | Public endpoint |
| `/hello-world/secure` | GET | JWT (ROLE_ADMIN) | Protected endpoint |
| `/swagger-ui/index.html` | GET | None | Interactive API documentation |
| `/actuator` | GET | None | Health and metrics endpoints |

### Authentication

To access secured endpoints:

1. Obtain a JWT token (implementation-specific)
2. Include the token in the Authorization header:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

See `docs/requests/User.http` for sample HTTP requests.

## Project Structure

```
SpringBootstrap/
├── build.gradle.kts                    # Gradle build configuration (Kotlin DSL)
├── settings.gradle.kts                 # Gradle settings
├── Dockerfile                          # Multi-stage Docker build with custom JRE
├── docker-compose.yml                  # Local PostgreSQL service
│
├── src/
│   ├── main/
│   │   ├── kotlin/com/renato/springbootstrap/
│   │   │   ├── SpringBootstrapApplication.kt    # Application entry point
│   │   │   ├── api/                             # External API integrations
│   │   │   ├── security/                        # Security config, JWT, auth services
│   │   │   ├── helloworld/controller/           # Sample REST controllers
│   │   │   └── exception/                       # Global exception handling
│   │   │
│   │   └── resources/
│   │       ├── application.yml                  # Default config (H2, ports, JWT)
│   │       ├── application-render.yml           # Render.com deployment profile
│   │       └── db/changelog/                    # Liquibase migration scripts
│   │
│   └── test/kotlin/                    # Unit and integration tests
│
└── docs/requests/User.http            # Sample HTTP requests
```

## CI/CD

### GitHub Actions Pipeline

The project uses automated CI/CD via `.github/workflows/build.yml`:

**Build & Test:**
- Compiles with JDK 24
- Runs full test suite
- Generates code coverage reports

**Quality & Security:**
- Uploads coverage to [Codecov](https://codecov.io)
- Scans Docker images with [Snyk](https://snyk.io)

**Deployment:**
- Builds optimized Docker images
- Publishes to GitHub Container Registry
- Automatically prunes old image versions

### Required Secrets

Configure these secrets in your repository settings:

| Secret | Purpose |
|--------|---------|
| `CODECOV_TOKEN` | Upload coverage reports to Codecov |
| `SNYK_TOKEN` | Security scanning with Snyk |

## Security

For security policies, vulnerability reporting, and best practices, please see [SECURITY.md](SECURITY.md).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

See [LICENSE](LICENSE) for full terms.

---

**Questions or Issues?** Please [open an issue](https://github.com/fialhorenato/SpringBootstrap/issues) on GitHub.
