plugins {
  id("lib")
  id("maven-publish")
}

java {
  withJavadocJar()
  withSourcesJar()
}

publishing {
  publications {
    create<MavenPublication>("jklee") {
      from(components["java"])
      pom {
        description = "Jklee integrates async-profiler in spring-boot application"
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
            url = "https://opensource.org/license/MIT"
          }
        }
        scm {
          connection = "scm:git:https://github.com/genuss/jklee.git"
          url = "https://github.com/genuss/jklee"
        }
        url = "https://github.com/genuss/jklee"
      }
    }
  }
  repositories {
    maven { url = rootProject.layout.projectDirectory.dir("staging-repo").asFile.toURI() }
  }
}
