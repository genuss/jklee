plugins {
  id("com.diffplug.spotless")
  id("fr.brouillard.oss.gradle.jgitver")
  id("idea")
  id("maven-publish")
}

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
  options.release = 17
  options.compilerArgs = listOf("-Werror", "-parameters")
  targetCompatibility = JavaVersion.VERSION_17.toString()
  sourceCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.publish { doLast { println("Version was ${project.version}") } }
