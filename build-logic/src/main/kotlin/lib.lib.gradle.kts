plugins {
  id("core.lib")
  id("java-library")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

java {
  toolchain { languageVersion = JavaLanguageVersion.of(libs.findVersion("java").get().requiredVersion) }
  withJavadocJar()
  withSourcesJar()
}


publishing {
  publications {
    create<MavenPublication>("jklee") {
      from(components["java"])
      groupId = project.group.toString()
    }
  }
}

spotless {
  java {
    googleJavaFormat("1.16.0")
    targetExclude("src/main/java/one/profiler/**/*.java")
  }
}
