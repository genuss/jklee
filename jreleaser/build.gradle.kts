plugins {
  id("core")
  id("org.jreleaser") version "1.15.0"
}

jreleaser {
  //  signing {
  //    active = org.jreleaser.model.Active.ALWAYS
  //    armored = true
  //  }
  // dryrun = true
  gitRootSearch = true
  strict = true
  project {
    versionPattern = "CUSTOM"
    // placeholder to calm down formatter
    description = "Jklee integrates async-profiler in spring-boot application"
    author("Alexey Genus")
    inceptionYear = "2024"
  }
  release {
    github {
      username = providers.environmentVariable("GITHUB_ACTOR").getOrElse("anonymous")
      token = providers.environmentVariable("GITHUB_TOKEN").getOrElse("anonymous")
    }
  }
  deploy {
    maven {
      mavenCentral {
        create("jklee") {
          active = org.jreleaser.model.Active.ALWAYS
          password = providers.environmentVariable("OSS_SONATYPE_PASSWORD").getOrElse("anonymous")
          stagingRepository(
              rootProject.layout.buildDirectory.dir("stage-repo").get().asFile.absolutePath)
          url = "https://central.sonatype.com/api/v1/publisher"
          username = providers.environmentVariable("OSS_SONATYPE_USER").getOrElse("anonymous")
          sign = false
        }
      }
    }
  }
}
