rootProject.name = "jklee"

include("core")

include("spring-boot")

include("samples")

include("spring-boot-admin")

pluginManagement {
  val jkleeSpringBootVersion: String by settings
  plugins { id("org.springframework.boot") version jkleeSpringBootVersion }

  resolutionStrategy {
    val credentials: (PasswordCredentials).() -> Unit = {
      username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
      password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
    }
    repositories {
      maven("https://nexus.px019.net/repository/plugins.gradle.org_m2") { credentials(credentials) }
    }
  }
}

dependencyResolutionManagement {
  val credentials: (PasswordCredentials).() -> Unit = {
    username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
    password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
  }
  @Suppress("UnstableApiUsage")
  repositories {
    maven("https://nexus.px019.net/repository/maven-central/") { credentials(credentials) }
  }
}
