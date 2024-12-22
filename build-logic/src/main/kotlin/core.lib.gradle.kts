plugins {
  id("com.diffplug.spotless")
  id("fr.brouillard.oss.gradle.jgitver")
  id("idea")
  id("maven-publish")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

group = "me.genuss.jklee"

jgitver {
  failIfDirty = (System.getenv("CI") ?: "false").toBooleanStrict()
  useDirty = true
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
