@file:Suppress("UnstableApiUsage")

rootProject.name = "jklee"

include("core")

include("spring-boot")

include("samples")

include("spring-boot-admin")

pluginManagement {
  includeBuild("build-logic")

  repositories {
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}
