plugins {
  id("com.diffplug.spotless")
  id("idea")
}

group = "me.genuss.jklee"

spotless { kotlinGradle { ktfmt() } }
