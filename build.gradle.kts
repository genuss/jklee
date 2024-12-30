plugins {
  id("core")
  id("org.jreleaser") version "1.15.0"
  id("pl.allegro.tech.build.axion-release") version "1.18.16"
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
  strict = true
  project {
    author("Alexey Genus")
    description = "Jklee integrates async-profiler in spring-boot application"
    inceptionYear = "2024"
    versionPattern = "CUSTOM"
  }
  release {
    github {
      overwrite = true
      prerelease { enabled = true }
      signatures = true
      skipTag = true
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
          active = org.jreleaser.model.Active.RELEASE
          sign = true
          stage = org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer.Stage.UPLOAD
          stagingRepository(stageRepoPath)
          url = "https://central.sonatype.com/api/v1/publisher"
        }
      }
    }
  }
}

scmVersion {
  snapshotCreator { _, _ -> "" }
  versionCreator { versionFromTag, position ->
    if (!position.isClean &&
        providers.environmentVariable("CI").map(String::toBoolean).getOrElse(false)) {
      throw IllegalStateException("Cannot release dirty version in CI")
    }
    val revision =
        providers.environmentVariable("GITHUB_SHA").orElse(position.revision).getOrElse("unknown")
    val revisionSuffix = "-$revision"
    val dirtySuffix = if (position.isClean) "" else "-dirty"
    val runNumber = providers.environmentVariable("GITHUB_RUN_NUMBER").getOrElse("000000")
    val runNumberSuffix = "-${runNumber.padStart(6, '0')}"

    if (position.branch != "master") {
      return@versionCreator "$versionFromTag$runNumberSuffix$revisionSuffix$dirtySuffix"
    }
    "$versionFromTag$dirtySuffix"
  }
}

version = scmVersion.version

allprojects { project.version = rootProject.version }
