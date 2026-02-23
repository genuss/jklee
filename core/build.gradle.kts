plugins { id("lib.publish") }

extra["javaVersion"] = libs.versions.javaCore.get()

dependencies {
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor(platform(libs.springBoot27))

  compileOnly("com.fasterxml.jackson.core:jackson-annotations")
  compileOnly("org.projectlombok:lombok")
  compileOnly(platform(libs.springBoot27))

  testAnnotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor(platform(libs.springBoot27))

  testCompileOnly("com.fasterxml.jackson.core:jackson-annotations")
  testCompileOnly("org.projectlombok:lombok")
  testCompileOnly(platform(libs.springBoot27))

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation(platform(libs.springBoot27))
}
