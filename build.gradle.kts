plugins {
  id("com.diffplug.spotless") version "6.18.0"
  id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
  id("java")
  id("maven-publish")
}

val jkleeSpringBootVersion: String by properties
val jkleeSpringBootAdminVersion: String by properties

allprojects {
  group = "me.genuss.jklee"
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
      googleJavaFormat("1.16.0")
      targetExclude("src/main/java/one/profiler/**/*.java")
    }
    kotlinGradle { ktfmt() }
    json {
      target("src/**/*.json")
      gson().sortByKeys().indentWithSpaces(2)
    }
  }

  java { withSourcesJar() }

  tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
    options.compilerArgs.add("-parameters")
  }

  tasks.withType<Test> { useJUnitPlatform() }

  publishing {
    publications {
      create<MavenPublication>("jklee") {
        groupId = rootProject.group as String
        artifactId = project.name
        from(project.components["java"])
      }
    }
    repositories {
      maven {
        url = uri("https://nexus.px019.net/repository/px019_capt_processing/")
        credentials {
          username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
          password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
        }
      }
    }
  }
}

jgitver { useDirty = true }
