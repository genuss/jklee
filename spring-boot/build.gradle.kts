plugins {
  id("core")
  id("lib")
}

dependencies {
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor(platform(libs.springBoot))

  api(project(":core"))

  compileOnly("com.fasterxml.jackson.core:jackson-annotations")
  compileOnly("org.projectlombok:lombok")
  compileOnly(platform(libs.springBoot))

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation(platform(libs.springBoot))

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(platform(libs.springBoot))
}
