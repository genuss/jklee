plugins {
  id("core")
  id("org.jreleaser") version "1.15.0"
}

val stageRepoPath: String =
    rootProject.layout.projectDirectory.dir("staging-repo").asFile.absolutePath

jreleaser {
  signing {
    active = org.jreleaser.model.Active.ALWAYS
    armored = true
    checksums = false
    mode = org.jreleaser.model.Signing.Mode.MEMORY
    verify = true
  }
  gitRootSearch = true
  strict = true
  project {
    author("Alexey Genus")
    description = "Jklee integrates async-profiler in spring-boot application"
    inceptionYear = "2024"
    versionPattern = "CUSTOM"
  }
  release {
    github {
      version = "0.0.0-dev"
      name = "Test Release"
      overwrite = true
      signatures = true
      update { skipTag = true }
      sign = true
    }
  }
  deploy {
    maven {
      github {
        create("jklee") {
          active = org.jreleaser.model.Active.ALWAYS
          applyMavenCentralRules = true
          sign = true
          stagingRepository(stageRepoPath)
          url = "https://maven.pkg.github.com/genuss/jklee"
        }
      }
      mavenCentral {
        create("jklee") {
          active = org.jreleaser.model.Active.ALWAYS
          sign = true
          stage = org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer.Stage.UPLOAD
          stagingRepository(stageRepoPath)
          url = "https://central.sonatype.com/api/v1/publisher"
        }
      }
    }
  }
}
