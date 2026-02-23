# Downgrade spring-boot module to Spring Boot 2.7.18 and Java 8

## Overview

Downgrade the `spring-boot` module to use Spring Boot 2.7.18 and target Java 8, while keeping all other modules (spring-boot-admin, samples) on Spring Boot 3.1.5 and Java 17. This requires splitting the version catalog entries, fixing Java 8 language incompatibilities in source code, and adjusting the auto-configuration registration.

## Context

- Files involved:
  - `gradle/libs.versions.toml` - version catalog (add Spring Boot 2.7 entries)
  - `spring-boot/build.gradle.kts` - module build file (use new BOM reference)
  - `spring-boot/src/main/java/me/genuss/jklee/JkleeAutoConfiguration.java` - replace `var`
  - `spring-boot/src/main/java/me/genuss/jklee/JkleeConfigurationProperties.java` - adjust `@ConstructorBinding` for 2.7
  - `spring-boot/src/main/java/me/genuss/jklee/JkleeProfileEndpoint.java` - replace `var`, `Map.of()`, `.toList()`
  - `spring-boot/src/main/java/me/genuss/jklee/JkleeSettingsEndpoint.java` - replace `var`
  - `spring-boot/src/main/resources/META-INF/spring.factories` - add for 2.7 compatibility
- Related patterns: The `core` module already targets Java 8, so cross-compilation with `--release 8` is proven to work
- Dependencies: Spring Boot 2.7.18 BOM

## Development Approach

- **Testing approach**: Regular (code first, then tests)
- Complete each task fully before moving to the next
- The spring-boot module has no existing tests, so verification will be via build compilation
- **CRITICAL: all modules must build successfully before considering a task complete**

## Implementation Steps

### Task 1: Update version catalog and build files

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `spring-boot/build.gradle.kts`

- [x] Add `springBoot27 = "2.7.18"` version entry to `gradle/libs.versions.toml`
- [x] Add library entries for Spring Boot 2.7 BOM: `springBoot27 = { group = "org.springframework.boot", name = "spring-boot-dependencies", version.ref = "springBoot27" }`
- [x] Change `javaSpringBoot` from `"17"` to `"8"` in versions
- [x] Update `spring-boot/build.gradle.kts` to use `libs.springBoot27` instead of `libs.springBoot` in all dependency declarations (platform BOM references and annotation processor)
- [x] Run `./gradlew :spring-boot:dependencies` to verify dependency resolution

### Task 2: Fix Java 8 language incompatibilities in source code

**Files:**
- Modify: `spring-boot/src/main/java/me/genuss/jklee/JkleeAutoConfiguration.java`
- Modify: `spring-boot/src/main/java/me/genuss/jklee/JkleeProfileEndpoint.java`
- Modify: `spring-boot/src/main/java/me/genuss/jklee/JkleeSettingsEndpoint.java`

- [x] `JkleeAutoConfiguration.java` line 16: replace `var asyncProfiler` with `JkleeConfigurationProperties.AsyncProfiler asyncProfiler`
- [x] `JkleeProfileEndpoint.java` lines 28-31: replace `var formats` with explicit `Stream<Map<String, String>>` type, replace `Map.of("label", ...)` with a helper or `Collections.singletonMap` alternative (use a two-entry HashMap or a utility), replace `.toList()` with `.collect(Collectors.toList())`
- [x] `JkleeSettingsEndpoint.java` lines 21-22, 27-28: replace all `var` usages with explicit types (`JkleeSettings settings`, `ArrayList<ConfigEntry> settingsMap`, `String candidate`, etc.)
- [x] Run `./gradlew spotlessApply` to fix formatting
- [x] Run `./gradlew :spring-boot:compileJava` to verify compilation with Java 8 target

### Task 3: Adjust @ConstructorBinding for Spring Boot 2.7

**Files:**
- Modify: `spring-boot/src/main/java/me/genuss/jklee/JkleeConfigurationProperties.java`

- [ ] Move `@ConstructorBinding` from constructor level to class level (Spring Boot 2.7 canonical style) - place it on `JkleeConfigurationProperties` class and `AsyncProfiler` inner class
- [ ] Change import from `org.springframework.boot.context.properties.ConstructorBinding` (used as FQCN in current code) to class-level annotation import `org.springframework.boot.context.properties.ConstructorBinding`
- [ ] Remove `@SuppressWarnings("removal")` annotation (not needed in 2.7)
- [ ] Run `./gradlew spotlessApply` then `./gradlew :spring-boot:compileJava` to verify

### Task 4: Add spring.factories for auto-configuration registration

**Files:**
- Create: `spring-boot/src/main/resources/META-INF/spring.factories`

- [ ] Create `spring-boot/src/main/resources/META-INF/spring.factories` with content:
  ```
  org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  me.genuss.jklee.JkleeAutoConfiguration
  ```
- [ ] Keep the existing `AutoConfiguration.imports` file (Spring Boot 2.7 supports both mechanisms, keeping it provides forward compatibility)
- [ ] Run `./gradlew :spring-boot:compileJava` to verify

### Task 5: Verify full build

- [ ] Run `./gradlew build` to verify all modules compile and pass
- [ ] Run `./gradlew spotlessCheck` to verify code style
- [ ] Verify the samples module still compiles (it depends on `:spring-boot` and uses Spring Boot 3.1.5)

### Task 6: Update documentation

- [ ] Update CLAUDE.md if internal patterns changed (update Java version info for spring-boot module)
- [ ] Move this plan to `docs/plans/completed/`
