plugins { id("platform") }

dependencies {
  constraints {
    api(project(":core"))
    api(project(":spring-boot"))
    api(project(":spring-boot-admin"))
  }
}
