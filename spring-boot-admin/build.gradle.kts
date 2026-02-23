plugins {
  id("frontend")
  id("lib.publish")
}

extra["javaVersion"] = libs.versions.javaSpringBootAdmin.get()
