plugins {
  id("org.springframework.boot")
}

dependencies {
  implementation ("de.codecentric:spring-boot-admin-starter-client")
  implementation ("de.codecentric:spring-boot-admin-starter-server")

  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")

  implementation(project(":spring-boot"))
  implementation(project(":spring-boot-admin"))
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
  // when running with bootRun, user.dir property will be set to "samples" dir, but when running
  // inside an IDE it will be usually set to project.rootDir, so to preserve consistency we set
  // the property here.
  systemProperty("spring.boot.admin.ui.extension-resource-locations", "file:${project.rootDir}/spring-boot-admin/build/resources/main/META-INF/spring-boot-admin-server-ui/extensions/custom/")
}
