rootProject.name = "jklee"

include("core")

include("spring-boot")

include("samples")

include("spring-boot-admin")

pluginManagement {
  val jkleeSpringBootVersion: String by settings
  plugins { id("org.springframework.boot") version jkleeSpringBootVersion }
}
