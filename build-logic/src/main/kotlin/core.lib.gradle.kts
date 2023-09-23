plugins {
  id("com.diffplug.spotless")
  id("fr.brouillard.oss.gradle.jgitver")
  id("idea")
  id("java-library")
  id("maven-publish")
}


group = "me.genuss.jklee"

java { withSourcesJar() }

jgitver {
  failIfDirty = (System.getenv("CI") ?: "false").toBooleanStrict()
  useDirty = true
}

publishing {
  publications {
    create<MavenPublication>("jklee") {
      from(components["java"])
      groupId = project.group.toString()
    }
  }
  repositories {
    maven {
      url = uri("https://nexus.px019.net/repository/px019_capt_processing/")
      credentials {
        username = System.getenv("NEXUS_USER") ?: System.getProperty("NEXUS_USER")
        password = System.getenv("NEXUS_PASS") ?: System.getProperty("NEXUS_PASS")
      }
    }
  }
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
  options.release.set(17)
  options.compilerArgs.add("-parameters")
  targetCompatibility = JavaVersion.VERSION_17.toString()
  sourceCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.publish { doLast { println("Version was ${project.version}") } }
