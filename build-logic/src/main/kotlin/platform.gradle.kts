plugins {
  id("java-platform")
  id("maven-publish")
}

javaPlatform { allowDependencies() }

publishing {
  publications {
    create<MavenPublication>("jklee") {
      from(components["javaPlatform"])
      pom {
        description = "Jklee BOM"
        developers {
          developer {
            id = "genuss"
            name = "Alexey Genus"
          }
        }
        name = project.name
        licenses {
          license {
            name = "MIT License"
            url = "https://opensource.org/licenses/MIT"
          }
        }
        scm { connection = "scm:git:git://github.com/genuss/jklee.git" }
        url = "https://github.com/genuss/jklee"
      }
    }
  }
  repositories {
    maven { url = rootProject.layout.buildDirectory.dir("stage-repo").get().asFile.toURI() }
  }
}
