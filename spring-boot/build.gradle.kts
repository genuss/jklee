plugins { `java-library` }

dependencies {
  compileOnly("com.fasterxml.jackson.core:jackson-annotations")
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  api(project(":core"))

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
