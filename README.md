# Spring Boot(strap) with Kotlin

[![Build](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml/badge.svg)](https://github.com/fialhorenato/SpringBootstrap/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/fialhorenato/SpringBootstrap/branch/main/graph/badge.svg?token=8ATZXFJK2Q)](https://codecov.io/gh/fialhorenato/SpringBootstrap)

The project can be seen running [here](https://spring-bootstrap.onrender.com/swagger-ui/index.html)

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
