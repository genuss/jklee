@file:Suppress("UnstableApiUsage")

rootProject.name = "jklee"

include("bom")

include("core")

include("spring-boot")

include("samples")

include("spring-boot-admin")

include("integration-tests:spring-boot-3.5")

pluginManagement {
  includeBuild("build-logic")

  repositories { gradlePluginPortal() }
}

dependencyResolutionManagement { repositories { mavenCentral() } }
