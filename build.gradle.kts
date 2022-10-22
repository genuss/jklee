plugins {
  java
  id("com.diffplug.spotless") version "6.11.0"
}

val jkleeSpringBootVersion: String by properties
val jkleeSpringBootAdminVersion: String by properties

allprojects {
  repositories { mavenCentral() }

  apply {
    plugin("java")
    plugin("com.diffplug.spotless")
  }
  dependencies {
    implementation(
        platform("de.codecentric:spring-boot-admin-dependencies:$jkleeSpringBootAdminVersion"))
    implementation(
        platform("org.springframework.boot:spring-boot-dependencies:$jkleeSpringBootVersion"))
    testImplementation(
        platform("org.springframework.boot:spring-boot-dependencies:$jkleeSpringBootVersion"))
    annotationProcessor(
        platform("org.springframework.boot:spring-boot-dependencies:$jkleeSpringBootVersion"))
    testAnnotationProcessor(
        platform("org.springframework.boot:spring-boot-dependencies:$jkleeSpringBootVersion"))
  }

  spotless {
    java {
      googleJavaFormat("1.15.0")
      targetExclude("src/main/java/one/profiler/**/*.java")
    }
    kotlinGradle { ktfmt() }
    json {
      target("src/**/*.json")
      gson().sortByKeys().indentWithSpaces(2)
    }
  }

  java { toolchain { languageVersion.set(JavaLanguageVersion.of(11)) } }

  tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
    options.compilerArgs.add("-parameters")
  }

  tasks.named<Test>("test") { useJUnitPlatform() }
}
