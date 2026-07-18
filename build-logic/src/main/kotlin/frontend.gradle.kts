plugins { id("org.siouan.frontend-jdk11") }

frontend {
  nodeVersion = "18.16.0"
  assembleScript = "run build"
  checkScript = "run test"
}

tasks.register<org.siouan.frontendgradleplugin.infrastructure.gradle.RunNpm>("npmWatch") {
  script = "run build:dev"
}
