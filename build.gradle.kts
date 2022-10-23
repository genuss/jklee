plugins {
  `java`
  `maven-publish`
  id("com.diffplug.spotless") version "6.11.0"
  id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
}

group = "me.genuss.jklee"

val jkleeSpringBootVersion: String by properties
val jkleeSpringBootAdminVersion: String by properties

allprojects {
  repositories { mavenCentral() }

  apply {
    plugin("com.diffplug.spotless")
    plugin("fr.brouillard.oss.gradle.jgitver")
    plugin("java")
    plugin("maven-publish")
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

  java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(11)) }
    withSourcesJar()
  }

  tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
    options.compilerArgs.add("-parameters")
  }

  tasks.named<Test>("test") { useJUnitPlatform() }

  publishing {
    publications {
      create<MavenPublication>("jklee") {
        groupId = rootProject.group as String
        artifactId = project.name
        from(project.components["java"])
      }
    }
  }
  repositories {
    maven {
      url = uri("https://nexus.px019.net/repository/px019_core/")
      credentials {
        username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
        password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
      }
    }
  }
}

jgitver { useDirty = true }
