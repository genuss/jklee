plugins {
  id("com.diffplug.spotless")
  id("fr.brouillard.oss.gradle.jgitver")
  id("idea")
}

group = "me.genuss.jklee"

jgitver {
  failIfDirty = providers.environmentVariable("CI").map { it.toBooleanStrict() }.getOrElse(false)
  useDirty = true
}

spotless { kotlinGradle { ktfmt() } }
