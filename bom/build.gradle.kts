plugins {
  id("core.lib")
  id("java-platform")
}

javaPlatform { allowDependencies() }

dependencies {
  constraints {
    api(project(":core"))
    api(project(":samples"))
    api(project(":spring-boot"))
    api(project(":spring-boot-admin"))
  }
}

publishing {
  publications { create<MavenPublication>("jklee") { from(components["javaPlatform"]) } }
}
