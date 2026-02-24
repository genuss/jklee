plugins {
  id("lib")
  alias(libs.plugins.springBoot)
}

extra["javaVersion"] = libs.versions.javaSpringBootAdmin.get()

dependencies {
  implementation("de.codecentric:spring-boot-admin-starter-client")
  implementation("de.codecentric:spring-boot-admin-starter-server")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation(platform(libs.springBoot40))
  implementation(platform(libs.springBootAdmin))
  implementation(project(":spring-boot"))
  implementation(project(":spring-boot-admin"))

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(platform(libs.springBoot40))

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
  test {
    systemProperty(
        "jklee.libs.dir",
        rootProject.layout.projectDirectory.dir("libs").asFile.absolutePath,
    )
  }
}
