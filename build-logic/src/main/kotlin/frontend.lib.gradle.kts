plugins {
  id("org.siouan.frontend-jdk11")
}


frontend {
  nodeVersion = "18.16.0"
  assembleScript = "run build"
  nodeDistributionUrlRoot = "https://nexus.px019.net/repository/nodejs-org-dist/"
}

tasks.register<org.siouan.frontendgradleplugin.infrastructure.gradle.RunNpm>("npmWatch") {
  script = "run build:dev"
}
