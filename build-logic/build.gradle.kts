plugins {
  `kotlin-dsl`
  id("com.diffplug.spotless") version libs.versions.spotless
}

dependencies {
  implementation(libs.jgitver.gradlePlugin)
  implementation(libs.spotless.gradlePlugin)
  implementation(libs.siouanFrontend.gradlePlugin)
  implementation(libs.springBoot.gradlePlugin)
}

spotless {
  kotlin {
    ktfmt()
    target("src/**/*.kt")
  }
  kotlinGradle { ktfmt() }
}
