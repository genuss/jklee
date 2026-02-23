plugins {
  id("com.diffplug.spotless")
  id("idea")
}

spotless {
  kotlinGradle {
    endWithNewline()
    ktfmt()
    trimTrailingWhitespace()
  }
}
