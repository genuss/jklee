plugins {
  `kotlin-dsl`
  id("com.diffplug.spotless") version libs.versions.spotless
}

dependencies {
  implementation(libs.axionRelease.gradlePlugin)
  implementation(libs.siouanFrontend.gradlePlugin)
  implementation(libs.spotless.gradlePlugin)
  implementation(libs.springBoot.gradlePlugin)
}

spotless {
  kotlinGradle {
    ktfmt()
    target("*.gradle.kts", "src/**/kotlin/*.kts")
  }
}
