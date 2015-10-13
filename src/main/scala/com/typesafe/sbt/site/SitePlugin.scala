package com.typesafe.sbt.site

import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._

object SitePlugin extends AutoPlugin {
  override def trigger = allRequirements
  object autoImport {
    val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
    val packageSite = TaskKey[File]("package-site", "Create a zip file of the website.")
    val siteSubdirName = SettingKey[String]("siteSubdirName",
      "Name of subdirectory in site target directory to put generator plugin content. Defaults to empty string.")
    val siteMappings = mappings in makeSite
    val siteDirectory = target in makeSite
    val siteSources = sources in makeSite
    val siteSourceDirectory = sourceDirectory in makeSite
    def publishSite(): SettingsDefinition = addArtifact(artifact in packageSite, packageSite)
  }
  import autoImport._

  override lazy val projectSettings = Seq(
    siteMappings := Seq.empty,
    siteMappings <<= siteMappings ?? Seq.empty,
    siteDirectory := target.value / "site",
    siteSourceDirectory := sourceDirectory.value / "site",
    includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf",
    siteMappings ++= SiteHelpers.selectSubpaths(siteSourceDirectory.value, (includeFilter in makeSite).value),
    makeSite := SiteHelpers.copySite(siteDirectory.value, streams.value.cacheDirectory, siteMappings.value),
    artifact in packageSite := SiteHelpers.siteArtifact(moduleName.value),
    artifactPath in packageSite <<= Defaults.artifactPathSetting(artifact in packageSite),
    packageSite := SiteHelpers.createSiteZip(makeSite.value, (artifactPath in packageSite).value, streams.value)
  )
}
