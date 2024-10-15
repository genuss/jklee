plugins {
  id("com.diffplug.spotless")
  id("fr.brouillard.oss.gradle.jgitver")
  id("idea")
  id("java-library")
  id("maven-publish")
}

group = "me.genuss.jklee"

java {
  withJavadocJar()
  withSourcesJar()
}

jgitver {
  failIfDirty = (System.getenv("CI") ?: "false").toBooleanStrict()
  useDirty = true
}

spotless {
  java {
    googleJavaFormat("1.16.0")
    targetExclude("src/main/java/one/profiler/**/*.java")
  }
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
