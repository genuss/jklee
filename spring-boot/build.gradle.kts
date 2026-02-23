plugins { id("lib.publish") }

extra["javaVersion"] = libs.versions.javaSpringBoot.get()

dependencies {
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor(platform(libs.springBoot27))

  api(project(":core"))

  compileOnly("com.fasterxml.jackson.core:jackson-annotations")
  compileOnly("org.projectlombok:lombok")
  compileOnly(platform(libs.springBoot27))

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation(platform(libs.springBoot27))

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(platform(libs.springBoot27))
}
