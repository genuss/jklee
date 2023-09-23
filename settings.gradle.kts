@file:Suppress("UnstableApiUsage")

rootProject.name = "jklee"

include("core")

include("spring-boot")

include("samples")

include("spring-boot-admin")

pluginManagement {
  includeBuild("build-logic")

  val credentials: (PasswordCredentials).() -> Unit = {
    username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
    password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
  }
  repositories {
    maven("https://nexus.px019.net/repository/plugins.gradle.org/") { credentials(credentials) }
  }
}

dependencyResolutionManagement {
  val credentials: (PasswordCredentials).() -> Unit = {
    username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
    password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
  }
  repositories {
    maven("https://nexus.px019.net/repository/maven-central/") { credentials(credentials) }
  }
}
