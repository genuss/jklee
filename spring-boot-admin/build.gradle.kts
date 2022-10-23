plugins { id("org.siouan.frontend-jdk11") version "6.0.0" }

dependencies {
  implementation("de.codecentric:spring-boot-admin-starter-client")
  implementation("de.codecentric:spring-boot-admin-starter-server")

  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
}

frontend {
  nodeVersion.set("16.13.2")
  assembleScript.set("run build")
  nodeDistributionUrlRoot.set("https://nexus.px019.net/repository/nodejs-org-dist/")
}

tasks.register<org.siouan.frontendgradleplugin.infrastructure.gradle.RunNpm>("npmWatch") {
  script.set("run watch")
}
