plugins {
  id("boot.lib")
  id("core.lib")
}

dependencies {
  implementation("de.codecentric:spring-boot-admin-starter-client")
  implementation("de.codecentric:spring-boot-admin-starter-server")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation(platform(libs.springBoot))
  implementation(platform(libs.springBootAdmin))
  implementation(project(":spring-boot"))
  implementation(project(":spring-boot-admin"))
}
