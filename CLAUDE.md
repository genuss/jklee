# CLAUDE.md

## Project Overview

Jklee is a Java library that
wraps [async-profiler](https://github.com/async-profiler/async-profiler) for easy CPU/memory
profiling from JVM applications. It provides a core Java API, Spring Boot integration with actuator
endpoints, and a Spring Boot Admin UI plugin (Vue.js).

## Build & Test

```bash
# Build the entire project
./gradlew build

# Run tests
./gradlew test

# Run the sample Spring Boot app
./gradlew :samples:bootRun

# Run integration tests (Spring Boot compatibility)
./gradlew :integration-tests:spring-boot-2.7:test :integration-tests:spring-boot-3.5:test :integration-tests:spring-boot-4.0:test

# Frontend dev mode (spring-boot-admin module)
./gradlew :spring-boot-admin:npmWatch
```

Requires **Java 21+** (build JVM). Java target versions are per-module and defined in
`gradle/libs.versions.toml`:

- **core**, **spring-boot**: Java 8
- **spring-boot-admin**, **samples**, **integration-tests**: Java 17

The Gradle wrapper is included.

## Project Structure

- **core/** - Main Java library (no Spring dependency). Core API: `Jklee.java`, `JkleeSettings.java`
- **spring-boot/** - Spring Boot auto-configuration and actuator endpoints
- **spring-boot-admin/** - Vue 3 frontend plugin for Spring Boot Admin
- **samples/** - Example Spring Boot application
- **integration-tests/** - Integration test modules verifying Spring Boot auto-configuration across versions (2.7, 3.5, 4.0)
- **libs/async-profiler/** - Pre-built async-profiler v4.3 native libraries (macOS, Linux x64, Linux arm64) for integration tests
- **bom/** - Bill of Materials for dependency management
- **build-logic/** - Shared Gradle convention plugins

## Code Style

- Google Java Format enforced via Spotless (`./gradlew spotlessApply` to fix)
- Kotlin files formatted with ktfmt
- Lombok is used for builders, value types, and logging
- Compiler flags: `-Werror` (warnings are errors), `-Xlint:-options` (suppress cross-compilation
  warnings), `-parameters`

## Key Dependencies

- Java 8+ (core, spring-boot), Java 17+ (spring-boot-admin, samples), Gradle 8.x, JUnit 5
- Spring Boot 2.7.18 (spring-boot), Spring Boot 3.1.5 (spring-boot-admin, samples), Spring Boot Admin 3.1.7
- Tested against: Spring Boot 2.7.18, 3.5.11, and 4.0.3 (integration-tests)
- Vue 3.3.4, Vite 4.4.9 (frontend)
- Version catalog: `gradle/libs.versions.toml`

## Publishing

- Versioning via axion-release plugin (Git tag based)
- PR builds publish to GitHub Packages
- Master builds publish to GitHub Packages + Maven Central via JReleaser
