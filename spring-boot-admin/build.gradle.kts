plugins {
  id("core")
  id("frontend")
  id("lib")
}

extra["javaVersion"] = libs.versions.javaSpringBootAdmin.get()
