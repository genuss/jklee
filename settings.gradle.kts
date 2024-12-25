@file:Suppress("UnstableApiUsage")

rootProject.name = "jklee"

include("bom")

include("core")

include("jreleaser")

include("spring-boot")

include("samples")

include("spring-boot-admin")

pluginManagement {
  includeBuild("build-logic")

  repositories { gradlePluginPortal() }
}

dependencyResolutionManagement { repositories { mavenCentral() } }
