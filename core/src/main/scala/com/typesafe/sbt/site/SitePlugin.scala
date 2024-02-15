package com.typesafe.sbt.site

import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._

/** Primary plugin for generating static website in sbt. Automatically loads when added to project.. */
object SitePlugin extends AutoPlugin {
  override def trigger = allRequirements
  object autoImport extends SiteKeys {
    def publishSite(): SettingsDefinition = addArtifact(packageSite / artifact, packageSite)
    val addMappingsToSiteDir              = com.typesafe.sbt.site.util.SiteHelpers.addMappingsToSiteDir _
  }
  import autoImport._

  override lazy val projectSettings = Seq(
    siteMappings             := Seq.empty,
    siteMappings             := (siteMappings ?? Seq.empty).value,
    siteDirectory            := target.value / "site",
    siteSourceDirectory      := sourceDirectory.value / "site",
    makeSite / includeFilter := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf",
    siteMappings ++= SiteHelpers.selectSubpaths(siteSourceDirectory.value, (makeSite / includeFilter).value),
    makeSite := SiteHelpers.copySite(siteDirectory.value, streams.value.cacheDirectory, siteMappings.value),
    packageSite / artifact     := SiteHelpers.siteArtifact(moduleName.value),
    packageSite / artifactPath := Defaults.artifactPathSetting(packageSite / artifact).value,
    packageSite := SiteHelpers.createSiteZip(makeSite.value, (packageSite / artifactPath).value, streams.value)
  )
}
