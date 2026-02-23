plugins {
  id("core")
  id("java-library")
}

val javaVersion = libs.versions.javaSpringBootAdmin.get().toInt()

tasks {
  test {
    useJUnitPlatform()
    systemProperty(
        "jklee.libs.dir", rootProject.layout.projectDirectory.dir("libs").asFile.absolutePath)
  }
  withType<JavaCompile>().configureEach {
    options.release = javaVersion
    options.compilerArgs = listOf("-Werror", "-Xlint:-options", "-parameters")
  }
}

dependencies {
  testImplementation(project(":spring-boot"))

  testImplementation("org.springframework.boot:spring-boot-starter-actuator")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(platform(libs.springBoot40))

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
