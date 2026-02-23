plugins { id("integration-test") }

extra["javaVersion"] = libs.versions.javaIntegrationTests.get()

dependencies { testImplementation(platform(libs.springBoot27)) }
