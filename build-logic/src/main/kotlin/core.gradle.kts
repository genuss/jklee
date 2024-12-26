plugins {
  id("com.diffplug.spotless")
  id("idea")
  id("pl.allegro.tech.build.axion-release")
}

scmVersion {
  snapshotCreator { _, _ -> "" }
  versionCreator {
      versionFromTag: String,
      position: pl.allegro.tech.build.axion.release.domain.scm.ScmPosition ->
    if (!position.isClean &&
        (providers.environmentVariable("CI").map { it.toBooleanStrict() }.getOrElse(false))) {
      throw IllegalStateException("Cannot release dirty version in CI")
    }
    val revision =
        providers
            .environmentVariable("GITHUB_SHA")
            .orElse(position.shortRevision)
            .getOrElse("unknown")
    val revisionSuffix = "-$revision"
    val dirtySuffix = if (position.isClean) "" else "-dirty"
    val runNumber = providers.environmentVariable("GITHUB_RUN_NUMBER").getOrElse("000000")
    val runNumberSuffix = "-${runNumber.padStart(6, '0')}"

    if (position.branch != "master") {
      return@versionCreator "$versionFromTag$runNumberSuffix$revisionSuffix$dirtySuffix"
    }
    "$versionFromTag$dirtySuffix"
  }
}

group = "me.genuss.jklee"

version = scmVersion.version

spotless { kotlinGradle { ktfmt() } }
