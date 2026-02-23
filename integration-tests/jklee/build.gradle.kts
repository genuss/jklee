plugins { id("integration-test") }

extra["javaVersion"] = libs.versions.javaIntegrationTests.get()

dependencies {
  testImplementation(platform(libs.springBoot27))
  testImplementation(project(":core"))
  testImplementation("com.fasterxml.jackson.core:jackson-annotations")
  testImplementation("org.projectlombok:lombok")
}
