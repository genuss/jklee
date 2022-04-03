plugins {
  `java-library`
}

dependencies {
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  api(project(":core"))

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
}
