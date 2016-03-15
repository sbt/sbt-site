package com.typesafe.sbt.site

import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._

/** Primary plugin for generating static website in sbt. Automatically loads when added to project.. */
object SitePlugin extends AutoPlugin {
  override def trigger = allRequirements
  object autoImport extends SiteKeys {
    def publishSite(): SettingsDefinition = addArtifact(artifact in packageSite, packageSite)
    val addMappingsToSiteDir = com.typesafe.sbt.site.util.SiteHelpers.addMappingsToSiteDir _
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
