plugins {
  id("com.diffplug.spotless")
  id("fr.brouillard.oss.gradle.jgitver")
  id("idea")
  id("maven-publish")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

group = "me.genuss.jklee"

jgitver {
  failIfDirty = providers.environmentVariable("CI").map { it.toBooleanStrict() }.orElse(false).get()
  useDirty = true
}

publishing {
  repositories {
    maven {
      name = "GithubPackages"
      url = uri("https://maven.pkg.github.com/genuss/jklee")
      credentials {
        username = providers.environmentVariable("GITHUB_ACTOR").getOrElse("anonymous")
        password = providers.environmentVariable("GITHUB_TOKEN").getOrElse("anonymous")
      }
    }
  }
}

spotless {
  kotlinGradle { ktfmt() }
  json {
    target("src/**/*.json")
    gson().sortByKeys().indentWithSpaces(2)
  }
}

tasks.withType<JavaCompile> {
  options.release = libs.findVersion("java").get().requiredVersion.toInt()
  options.compilerArgs = listOf("-Werror", "-parameters")
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.publish { doLast { println("Version was ${project.version}") } }
