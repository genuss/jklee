@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

pluginManagement { repositories { gradlePluginPortal() } }

dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  versionCatalogs.create("libs").from(files("../gradle/libs.versions.toml"))
}
