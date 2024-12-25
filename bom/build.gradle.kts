plugins {
  id("core")
  id("platform")
}

dependencies {
  constraints {
    api(project(":core"))
    api(project(":samples"))
    api(project(":spring-boot"))
    api(project(":spring-boot-admin"))
  }
}
