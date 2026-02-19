# CLAUDE.md

## Project Overview

Jklee is a Java library that wraps [async-profiler](https://github.com/async-profiler/async-profiler) for easy CPU/memory profiling from JVM applications. It provides a core Java API, Spring Boot integration with actuator endpoints, and a Spring Boot Admin UI plugin (Vue.js).

## Build & Test

```bash
# Build the entire project
./gradlew build

# Run tests
./gradlew test

# Run the sample Spring Boot app
./gradlew :samples:bootRun

# Frontend dev mode (spring-boot-admin module)
./gradlew :spring-boot-admin:npmWatch
```

Requires **Java 17+**. The Gradle wrapper is included.

## Project Structure

- **core/** - Main Java library (no Spring dependency). Core API: `Jklee.java`, `JkleeSettings.java`
- **spring-boot/** - Spring Boot auto-configuration and actuator endpoints
- **spring-boot-admin/** - Vue 3 frontend plugin for Spring Boot Admin
- **samples/** - Example Spring Boot application
- **bom/** - Bill of Materials for dependency management
- **build-logic/** - Shared Gradle convention plugins

## Code Style

- Google Java Format enforced via Spotless (`./gradlew spotlessApply` to fix)
- Kotlin files formatted with ktfmt
- Lombok is used for builders, value types, and logging
- Compiler flags: `-Werror` (warnings are errors), `-parameters`

## Key Dependencies

- Java 17, Gradle 8.x, JUnit 5
- Spring Boot 3.1.5, Spring Boot Admin 3.1.7
- Vue 3.3.4, Vite 4.4.9 (frontend)
- Version catalog: `gradle/libs.versions.toml`

## Publishing

- Versioning via axion-release plugin (Git tag based)
- PR builds publish to GitHub Packages
- Master builds publish to GitHub Packages + Maven Central via JReleaser
