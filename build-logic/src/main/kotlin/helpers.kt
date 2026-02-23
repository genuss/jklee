import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.internal.extensions.core.extra
import kotlin.jvm.optionals.getOrNull

fun Project.getVersion(alias: String): String =
  extra[alias] as String? ?: error("Version $alias not found in catalog")

fun VersionCatalog.getVersion(alias: String): String =
  findVersion(alias).getOrNull()?.requiredVersion ?: error("Version $alias not found in catalog")
