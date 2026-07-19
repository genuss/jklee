plugins {
  id("core")
  id("java-library")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
  compileOnly(libs.findLibrary("jspecify").get())

  testCompileOnly(libs.findLibrary("jspecify").get())
}

spotless {
  java {
    endWithNewline()
    googleJavaFormat(libs.getVersion("googleJavaFormat"))
    targetExclude("src/main/java/one/profiler/**/*.java")
    trimTrailingWhitespace()
  }
}

tasks {
  withType<Test> { useJUnitPlatform() }
  withType<JavaCompile>().configureEach {
    val javaVersion = getJavaVersion()
    options.release = javaVersion.majorVersion.toInt()
    if (javaVersion.isJava8) {
      // https://github.com/jspecify/jspecify/issues/302
      options.compilerArgs = listOf("-Xlint:-options", "-parameters")
    } else {
      options.compilerArgs = listOf("-Werror", "-Xlint:-options", "-parameters")
    }
  }
  withType<Javadoc> { (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") }
}
