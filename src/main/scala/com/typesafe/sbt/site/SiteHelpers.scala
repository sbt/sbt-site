package com.typesafe.sbt.site

import java.io.PrintWriter

import sbt.Keys._
import sbt._
/**
 * Utility/support functions.
 */
object SiteHelpers {
  import SitePlugin.siteMappings
  /** Convenience functions to add a task of mappings to a site under a nested directory. */
  def addMappingsToSiteDir(
    mappings: TaskKey[Seq[(File, String)]],
    nestedDirectory: SettingKey[String]): Setting[_] =
    siteMappings <++= (mappings, nestedDirectory) map { (m, n) =>
      for ((f, d) <- m) yield (f, n + "/" + d)
    }

  def selectSubpaths(dir: File, filter: FileFilter): Seq[(File, String)] =
    Path.selectSubpaths(dir, filter).toSeq

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

  def directorySettings(config: Configuration): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        sourceDirectory := sourceDirectory.value / config.name,
        target := target.value / config.name
      ))
  def watchSettings(config: Configuration): Seq[Setting[_]] =
    Seq(
      watchSources in Global <++= (sourceDirectory in config) map {_.***.get}
    )
  def checkGems(requirements: Map[String, String], s: TaskStreams): Unit = {
    /** Generates an error message if the gem's version isn't appropriate for this build. */
    def makeError(
      gem: String,
      version: String,
      current: Option[String]): Option[String] = current match {
      case Some(v) if v contains version => None
      case Some(v) => Some(
        "This build requires the gem [%s (%s)] but found version (%s) instead." format(gem, version, v))
      case None => Some(
        "This build requires the gem [%s (%s)] to be installed." format(gem, version))
    }
    val errors = for {
      (gem, version) <- requirements
      error <- makeError(gem, version, getGemVersion(gem))
    } yield error
    if (errors.nonEmpty) {
      sys.error(errors.mkString("Gem version requirements failed:\n\t", "\n\t", "\n"))
    }
  }

  /** Checks versions of gems installed. */
  def getGemVersion(gem: String): Option[String] = {
    val installed = Seq("gem", "list", "--local", gem).!!
    """\((.+)\)""".r.findFirstMatchIn(installed) match {
      case None => None
      case Some(m) => Some(m.group(1))
    }
  }

  /**
   * Check whether a file has one of the given extensions.
   */
  def hasExtension(extensions: Set[String])(file: File): Boolean = {
    extensions contains file.ext
  }

  /**
   * Transform a file, line by line.
   */
  def transformFile(transform: String => String)(source: File, target: File): Unit = {
    IO.reader(source) { reader =>
      IO.writer(target, "", IO.defaultCharset) { writer =>
        val pw = new PrintWriter(writer)
        IO.foreachLine(reader) { line => pw.println(transform(line)) }
      }
    }
  }
}
