package com.typesafe.sbt

import sbt._
import Keys._
import com.typesafe.sbt.site._

object SbtSite extends AutoPlugin {
  override def trigger = allRequirements
  object autoImport {
    val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
    val packageSite = TaskKey[File]("package-site", "Create a zip file of the website.")
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

  object site {

//    def preprocessSite(alias: String = ""): Seq[Setting[_]] =
//      PreprocessSupport.settings() ++ Seq(addMappingsToSiteDir(mappings in PreprocessSupport.Preprocess, alias))
//
//    /** Includes scaladoc APIS in site under a "latest/api" directory. */
//    def includeScaladoc(alias: String = "latest/api"): Seq[Setting[_]] =
//      Seq(SiteHelpers.addMappingsToSiteDir(mappings in packageDoc in Compile, alias))
//    /** Includes Jekyll generated site under the root directory. */
//    def jekyllSupport(alias: String = ""): Seq[Setting[_]] =
//      JekyllSupport.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in JekyllSupport.autoImports.Jekyll, alias))
//    /** Includes Sphinx generated site under the root directory. */
//    def sphinxSupport(alias: String = ""): Seq[Setting[_]] =
//      SphinxSupport.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in SphinxSupport.Sphinx, alias))
//    /** Includes Pamflet generate site under the root directory. */
//    def pamfletSupport(alias: String = ""): Seq[Setting[_]] =
//      PamfletSupport.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in PamfletSupport.Pamflet, alias))
//    /** Includes Nanoc generated site under the root directory. */
//    def nanocSupport(alias: String = ""): Seq[Setting[_]] =
//      NanocSupport.settings() ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in NanocSupport.Nanoc, alias))
//    def asciidoctorSupport(alias: String = ""): Seq[Setting[_]] =
//      AsciidoctorSupport.settings ++ Seq(SiteHelpers.addMappingsToSiteDir(mappings in AsciidoctorSupport.Asciidoctor, alias))
    def publishSite(): SettingsDefinition = addArtifact(artifact in packageSite, packageSite)
  }

}
