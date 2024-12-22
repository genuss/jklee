plugins {
  id("core.lib")
  id("java-library")
}

java {
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
