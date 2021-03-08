# Spring Boot(strap) with Kotlin

![Java CI with Gradle](https://github.com/fialhorenato/SpringBootstrap/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=main) [![codecov](https://codecov.io/gh/fialhorenato/SpringBootstrap/branch/main/graph/badge.svg?token=8ATZXFJK2Q)](https://codecov.io/gh/fialhorenato/SpringBootstrap)

You can find the Swagger-ui of the application [here](https://springbootstrap.herokuapp.com/swagger-ui.html)

This is a basic project using Spring Boot, Kotlin and Gradle.

Those are the Spring modules used:

- Spring Security
- Spring Data JPA

External libraries:

- Flyway
- H2 (For local development)
- Springdoc UI

It is ready to be deployed using any Docker orchestrator technology (EKS, ECS, GKE, K8s)

## Github Actions

We currently use Github's docker registry, codecov and Snyk.

In order to work those, you must generate secrets for them and put in your repository secrets:

- CODECOV_TOKEN
- SNYK_TOKEN
- CR_PAT
