plugins {
  id("com.diffplug.spotless")
  id("java-library")
  id("maven-publish")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(libs.findVersion("java").get().requiredVersion)
  }
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

tasks {
  test { useJUnitPlatform() }
  withType<JavaCompile> {
    options.release = libs.findVersion("java").get().requiredVersion.toInt()
    options.compilerArgs = listOf("-Werror", "-parameters")
  }
  withType<Javadoc> { (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") }
}

spotless {
  java {
    googleJavaFormat("1.16.0")
    targetExclude("src/main/java/one/profiler/**/*.java")
  }
}
