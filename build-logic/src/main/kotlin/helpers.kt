import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.internal.extensions.core.extra
import kotlin.jvm.optionals.getOrNull

fun Project.getJavaVersion(): JavaVersion {
  val javaVersionsStr = extra["javaVersion"] as String? ?: error("javaVersion not found in project")
  return JavaVersion.toVersion(javaVersionsStr)
}

fun VersionCatalog.getVersion(alias: String): String =
  findVersion(alias).getOrNull()?.requiredVersion ?: error("Version $alias not found in catalog")
