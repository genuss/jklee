plugins {
  id("core")
  id("org.jreleaser") version "1.15.0"
}

jreleaser {
  configFile = layout.projectDirectory.file("jreleaser.yml")
  strict = true
}
