plugins {
  id("core")
  id("java-library")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

spotless {
  java {
    googleJavaFormat(libs.getVersion("googleJavaFormat"))
    targetExclude("src/main/java/one/profiler/**/*.java")
  }
}

tasks {
  withType<Test> { useJUnitPlatform() }
  withType<JavaCompile>().configureEach {
    options.release = getVersion("javaVersion").toInt()
    options.compilerArgs = listOf("-Werror", "-Xlint:-options", "-parameters")
  }
  withType<Javadoc> { (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") }
}
