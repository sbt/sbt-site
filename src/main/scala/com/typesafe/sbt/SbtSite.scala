package com.typesafe.sbt

import com.typesafe.sbt.site.AsciidoctorSupport
import sbt._
import Keys._
import com.typesafe.sbt.site._


object SbtSite extends Plugin {
  object SiteKeys {
    val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
    val packageSite = TaskKey[File]("package-site", "Create a zip file of the website.")

    // Helper to point at mappings for the site.
    val siteMappings = mappings in makeSite
    val siteDirectory = target in makeSite
    val siteSources = sources in makeSite
    val siteSourceDirectory = sourceDirectory in makeSite

    val previewSite = TaskKey[Unit]("preview-site", "Launches a jetty server that serves your generated site from the target directory")
    val previewFixedPort = SettingKey[Option[Int]]("previewFixedPort") in previewSite
    val previewLaunchBrowser = SettingKey[Boolean]("previewLaunchBrowser") in previewSite
  }

  object site {
    import SiteKeys._

    val settings = Seq(
      siteMappings := Seq.empty,
      siteDirectory := target.value / "site",
      siteSourceDirectory := sourceDirectory.value / "site",
      includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf",
      siteMappings ++= selectSubpaths(siteSourceDirectory.value, (includeFilter in makeSite).value),
      makeSite := copySite(siteDirectory.value, streams.value.cacheDirectory, siteMappings.value),
      artifact in packageSite := siteArtifact(moduleName.value),
      artifactPath in packageSite <<= Defaults.artifactPathSetting(artifact in packageSite),
      packageSite := createSiteZip(makeSite.value, (artifactPath in packageSite).value, streams.value)
    ) ++ Preview.settings

    /** Convenience functions to add a task of mappings to a site under a nested directory. */
    def addMappingsToSiteDir(mappings: TaskKey[Seq[(File,String)]], nestedDirectory: String): Setting[_] =
      siteMappings <++= mappings map { m =>
        for((f, d) <- m) yield (f, nestedDirectory + "/" + d)
      }

    /** Includes scaladoc APIS in site under a "latest/api" directory. */
    def includeScaladoc(alias: String = "latest/api"): Seq[Setting[_]] =
      Seq(addMappingsToSiteDir(mappings in packageDoc in Compile, alias))
    /** Includes Jekyll generated site under the root directory. */
    def jekyllSupport(alias: String = ""): Seq[Setting[_]] =
      JekyllSupport.settings() ++ Seq(addMappingsToSiteDir(mappings in JekyllSupport.Jekyll, alias))
    /** Includes Sphinx generated site under the root directory. */
    def sphinxSupport(alias: String = ""): Seq[Setting[_]] =
      SphinxSupport.settings() ++ Seq(addMappingsToSiteDir(mappings in SphinxSupport.Sphinx, alias))
    /** Includes Pamflet generate site under the root directory. */
    def pamfletSupport(alias: String = ""): Seq[Setting[_]] =
      PamfletSupport.settings() ++ Seq(addMappingsToSiteDir(mappings in PamfletSupport.Pamflet, alias))
    /** Includes Nanoc generated site under the root directory. */
    def nanocSupport(alias: String = ""): Seq[Setting[_]] =
      NanocSupport.settings() ++ Seq(addMappingsToSiteDir(mappings in NanocSupport.Nanoc, alias))
    def asciidoctorSupport(alias: String = ""): Seq[Setting[_]] =
      AsciidoctorSupport.settings ++ Seq(addMappingsToSiteDir(mappings in AsciidoctorSupport.Asciidoctor, alias))
    def preprocessSite(alias: String = ""): Seq[Setting[_]] =
      PreprocessSupport.settings() ++ Seq(addMappingsToSiteDir(mappings in PreprocessSupport.Preprocess, alias))
    def publishSite(): SettingsDefinition = addArtifact(artifact in packageSite, packageSite)
  }

  // Note: We include helpers so other plugins can 'plug in' to this one without requiring users to use/configure the site plugin.
  override val settings = Seq(
    SiteKeys.siteMappings <<= SiteKeys.siteMappings ?? Seq.empty
  )

  def selectSubpaths(dir: File, filter: FileFilter): Seq[(File, String)] = Path.selectSubpaths(dir, filter).toSeq

  def copySite(dir: File, cacheDir: File, maps: Seq[(File, String)]): File = {
    val concrete = maps map { case (file, dest) => (file, dir / dest) }
    Sync(cacheDir / "make-site")(concrete)
    dir
  }

  def siteArtifact(name: String) = Artifact(name, Artifact.DocType, "zip", "site")

  def createSiteZip(siteDir: File, zipPath: File, s: TaskStreams): File = {
    IO.zip(Path.allSubpaths(siteDir), zipPath)
    s.log.info("Site packaged: " + zipPath)
    zipPath
  }
}
