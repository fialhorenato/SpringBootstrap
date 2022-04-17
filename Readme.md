# Spring Boot(strap) with Kotlin

[![Build](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml/badge.svg)](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml)

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
