package com.typesafe.sbt.site

import sbt.Keys._
import sbt._

object SitePlugin extends AutoPlugin {
  override def trigger = allRequirements
  object autoImport {
    val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
    val packageSite = TaskKey[File]("package-site", "Create a zip file of the website.")
    val siteSubdirName = SettingKey[String]("siteSubdirName",
      "Name of subdirectory in site target directory to put generator plugin content. Defaults to empty string.")
    def publishSite(): SettingsDefinition = addArtifact(artifact in packageSite, packageSite)
  }
  import autoImport._

  // Helper to point at mappings for the site.
  private[sbt] val siteMappings = mappings in makeSite
  private[sbt] val siteDirectory = target in makeSite
  private[sbt] val siteSources = sources in makeSite
  private[sbt] val siteSourceDirectory = sourceDirectory in makeSite

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

  //    def preprocessSite(alias: String = ""): Seq[Setting[_]] =
  //      PreprocessPlugin.settings() ++ Seq(addMappingsToSiteDir(mappings in PreprocessPlugin.Preprocess, alias))
  //
  //    /** Includes scaladoc APIS in site under a "latest/api" directory. */
  //    def includeScaladoc(alias: String = "latest/api"): Seq[Setting[_]] =
  //      Seq(SiteHelpers.addMappingsToSiteDir(mappings in packageDoc in Compile, alias))
  //    /** Includes Jekyll generated site under the root directory. */
  //    def jekyllSupport(alias: String = ""): Seq[Setting[_]] =
  //      JekyllPlugin.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in JekyllPlugin.autoImports.Jekyll, alias))
  //    /** Includes Sphinx generated site under the root directory. */
  //    def sphinxSupport(alias: String = ""): Seq[Setting[_]] =
  //      SphinxPlugin.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in SphinxPlugin.Sphinx, alias))
  //    /** Includes Pamflet generate site under the root directory. */
  //    def pamfletSupport(alias: String = ""): Seq[Setting[_]] =
  //      PamfletPlugin.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in PamfletPlugin.Pamflet, alias))
  //    /** Includes Nanoc generated site under the root directory. */
  //    def nanocSupport(alias: String = ""): Seq[Setting[_]] =
  //      NanocPlugin.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in NanocPlugin.Nanoc, alias))
  //    def asciidoctorSupport(alias: String = ""): Seq[Setting[_]] =
  //      AsciidoctorPlugin.settings ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in AsciidoctorPlugin.Asciidoctor, alias))

}
