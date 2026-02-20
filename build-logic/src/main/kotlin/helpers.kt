import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

fun Project.getVersion(alias: String): String =
  extra[alias] as String? ?: error("Version $alias not found in catalog")
