# Add Integration Test Modules for Spring Boot Compatibility

## Overview

Create integration test submodules under `integration-tests/` that verify jklee's Spring Boot auto-configuration works correctly across Spring Boot 2.7.18, 3.5.11, and 4.0.3. Each module starts a Spring context with JkleeAutoConfiguration and asserts that Jklee, JkleeSettingsEndpoint beans exist and async-profiler native library is loaded. Async-profiler v4.3 native libraries are downloaded and stored in the repository.

## Context

- Files involved:
  - `settings.gradle.kts` - add new modules
  - `gradle/libs.versions.toml` - add Spring Boot version entries
  - `integration-tests/spring-boot-2.7/build.gradle.kts` - new
  - `integration-tests/spring-boot-3.5/build.gradle.kts` - new
  - `integration-tests/spring-boot-4.0/build.gradle.kts` - new
  - `integration-tests/spring-boot-*/src/test/java/...` - test classes
  - `libs/async-profiler/` - native libraries directory
- Related patterns: existing module build files use convention plugins (`core`, `lib`); spring-boot module depends on `platform(libs.springBoot)` for BOM management
- Dependencies: async-profiler v4.3 native libraries from GitHub releases

## Key Technical Considerations

### Auto-configuration registration

- Spring Boot 3.x+ uses `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Spring Boot 2.7.x uses `META-INF/spring.factories`
- The existing jklee spring-boot module only has the 3.x-style file. The 2.7 integration test module will need a `spring.factories` in its test resources to register JkleeAutoConfiguration.

### Binary compatibility

- The jklee spring-boot module is compiled against Spring Boot 3.1.5. `JkleeConfigurationProperties` uses `@org.springframework.boot.context.properties.ConstructorBinding` (the old package location), which is deprecated in 3.x and likely removed in 4.0.
- The 4.0 module may need to provide its own configuration class or use `@ImportAutoConfiguration` to work around removed APIs.
- If a version is truly incompatible at the binary level, the test will document that clearly rather than papering over it.

### Native library

- Download `async-profiler-4.3-macos.zip` and `async-profiler-4.3-linux-x64.tar.gz` from GitHub releases
- Extract the `libasyncProfiler.dylib` (macOS) and `libasyncProfiler.so` (Linux x64) to `libs/async-profiler/`
- Tests configure `jklee.async-profiler.agent-path-candidates` pointing to the appropriate library
- Add a Gradle task or script to download/extract these libs

### Java version

- Spring Boot 2.7.18 requires Java 8+, but the jklee spring-boot module targets Java 17 bytecode, so tests need Java 17+
- Spring Boot 4.0.3 requires Java 17+
- All integration test modules will use Java 17 as the compile/test target

## Development Approach

- **Testing approach**: TDD - the whole purpose is creating tests
- Complete each task fully before moving to the next
- **CRITICAL: every task MUST include new/updated tests**
- **CRITICAL: all tests must pass before starting next task**

## Implementation Steps

### Task 1: Download and store async-profiler native libraries

**Files:**
- Create: `libs/async-profiler/README.md` (documents the version and how to update)
- Create: `libs/async-profiler/macos/libasyncProfiler.dylib`
- Create: `libs/async-profiler/linux-x64/libasyncProfiler.so`
- Create: `libs/async-profiler/linux-arm64/libasyncProfiler.so`

- [x] Download async-profiler v4.3 release archives (`async-profiler-4.3-macos.zip`, `async-profiler-4.3-linux-x64.tar.gz`, `async-profiler-4.3-linux-arm64.tar.gz`)
- [x] Extract native libraries (`libasyncProfiler.dylib` for macOS, `libasyncProfiler.so` for Linux) into `libs/async-profiler/{platform}/`
- [x] Add a short `README.md` documenting the version and download source
- [x] Add `libs/async-profiler/` to `.gitignore` exception if needed (native binaries should be committed for CI reproducibility) or alternatively create a Gradle task to download them on demand

### Task 2: Set up integration-tests module structure and Spring Boot 3.5 test

**Files:**
- Modify: `settings.gradle.kts`
- Modify: `gradle/libs.versions.toml`
- Create: `integration-tests/spring-boot-3.5/build.gradle.kts`
- Create: `integration-tests/spring-boot-3.5/src/test/java/me/genuss/jklee/it/JkleeAutoConfigurationIT.java`
- Create: `integration-tests/spring-boot-3.5/src/test/resources/application.properties`

- [ ] Add Spring Boot 3.5.11 version to `gradle/libs.versions.toml`
- [ ] Add `include("integration-tests:spring-boot-3.5")` to `settings.gradle.kts`
- [ ] Create `build.gradle.kts` applying `core` plugin (not `lib` - no publishing needed), setting Java 17, depending on `:spring-boot` project and Spring Boot 3.5.11 BOM, with `spring-boot-starter-test` and `spring-boot-starter-actuator`
- [ ] Create `application.properties` configuring `jklee.async-profiler.agent-path-candidates` to point to the native library in `libs/async-profiler/`
- [ ] Write `JkleeAutoConfigurationIT.java` test class:
  - Uses `@SpringBootTest` with `@ImportAutoConfiguration(JkleeAutoConfiguration.class)`
  - Asserts `Jklee` bean exists in context
  - Asserts `JkleeSettingsEndpoint` bean exists in context
  - Calls `jklee.getSettings()` and asserts `loaded` is true (async-profiler native lib loaded)
- [ ] Run `./gradlew :integration-tests:spring-boot-3.5:test` and verify it passes

### Task 3: Add Spring Boot 2.7 integration test module

**Files:**
- Modify: `settings.gradle.kts`
- Modify: `gradle/libs.versions.toml`
- Create: `integration-tests/spring-boot-2.7/build.gradle.kts`
- Create: `integration-tests/spring-boot-2.7/src/test/java/me/genuss/jklee/it/JkleeAutoConfigurationIT.java`
- Create: `integration-tests/spring-boot-2.7/src/test/resources/application.properties`
- Create: `integration-tests/spring-boot-2.7/src/test/resources/META-INF/spring.factories`

- [ ] Add Spring Boot 2.7.18 version to `gradle/libs.versions.toml`
- [ ] Add `include("integration-tests:spring-boot-2.7")` to `settings.gradle.kts`
- [ ] Create `build.gradle.kts` with Spring Boot 2.7.18 BOM, depending on `:spring-boot` and `:core` projects
- [ ] Create `META-INF/spring.factories` in test resources registering `JkleeAutoConfiguration` (needed because 2.7 doesn't read the new imports file)
- [ ] Create test class and `application.properties` (similar to Task 2)
- [ ] Run `./gradlew :integration-tests:spring-boot-2.7:test` - if binary incompatibilities are found, document them and adjust the test expectations accordingly

### Task 4: Add Spring Boot 4.0 integration test module

**Files:**
- Modify: `settings.gradle.kts`
- Modify: `gradle/libs.versions.toml`
- Create: `integration-tests/spring-boot-4.0/build.gradle.kts`
- Create: `integration-tests/spring-boot-4.0/src/test/java/me/genuss/jklee/it/JkleeAutoConfigurationIT.java`
- Create: `integration-tests/spring-boot-4.0/src/test/resources/application.properties`

- [ ] Add Spring Boot 4.0.3 version to `gradle/libs.versions.toml`
- [ ] Add `include("integration-tests:spring-boot-4.0")` to `settings.gradle.kts`
- [ ] Create `build.gradle.kts` with Spring Boot 4.0.3 BOM, depending on `:spring-boot` and `:core` projects
- [ ] Create test class and `application.properties` (similar to Task 2)
- [ ] Run `./gradlew :integration-tests:spring-boot-4.0:test` - if binary incompatibilities arise (e.g., removed `@ConstructorBinding` at old package), document and adjust

### Task 5: Verify all tests and clean up

- [ ] Run `./gradlew build` to verify the full project still builds
- [ ] Run all integration tests: `./gradlew :integration-tests:spring-boot-2.7:test :integration-tests:spring-boot-3.5:test :integration-tests:spring-boot-4.0:test`
- [ ] Run `./gradlew spotlessApply` to ensure code formatting
- [ ] Verify no regressions in existing modules
