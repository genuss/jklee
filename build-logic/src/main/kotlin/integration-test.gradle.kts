plugins {
  id("core")
  id("lib")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val javaVersion = libs.findVersion("javaIntegrationTests").get().toString().toInt()

tasks {
  test {
    systemProperty(
        "jklee.libs.dir", rootProject.layout.projectDirectory.dir("libs").asFile.absolutePath)
  }
}

dependencies {
  testImplementation("org.springframework.boot:spring-boot-starter-actuator")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(project(":spring-boot"))

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
