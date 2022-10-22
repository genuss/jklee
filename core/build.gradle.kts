plugins { `java-library` }

dependencies {
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  testCompileOnly("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")
  compileOnly("com.fasterxml.jackson.core:jackson-annotations")

  testCompileOnly("com.fasterxml.jackson.core:jackson-annotations")
  testImplementation("org.junit.jupiter:junit-jupiter")
}
