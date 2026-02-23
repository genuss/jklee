plugins {
  `kotlin-dsl`
  id("com.diffplug.spotless") version libs.versions.spotless
}

dependencies {
  implementation(libs.siouanFrontend.gradlePlugin)
  implementation(libs.spotless.gradlePlugin)
}

spotless {
  kotlinGradle {
    ktfmt()
    target("*.gradle.kts", "src/**/kotlin/*.kts")
  }
}
