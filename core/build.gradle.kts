plugins { id("lib.publish") }

extra["javaVersion"] = libs.versions.javaCore.get()

dependencies {
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor(platform(libs.springBoot))

  compileOnly("com.fasterxml.jackson.core:jackson-annotations")
  compileOnly("org.projectlombok:lombok")
  compileOnly(platform(libs.springBoot))

  testAnnotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor(platform(libs.springBoot))

  testCompileOnly("com.fasterxml.jackson.core:jackson-annotations")
  testCompileOnly("org.projectlombok:lombok")
  testCompileOnly(platform(libs.springBoot))

  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation(platform(libs.springBoot))
}
