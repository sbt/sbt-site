package com.typesafe.sbt.site.util

import java.io.PrintWriter

import com.typesafe.sbt.site.{Compat, SitePlugin}
import com.typesafe.sbt.site.Compat._
import sbt.Keys._
import sbt._
/**
 * Utility/support functions.
 */
object SiteHelpers {
  import SitePlugin.autoImport.siteMappings
  /** Convenience functions to add a task of mappings to a site under a nested directory. */
  def addMappingsToSiteDir(
    mappings: TaskKey[Seq[(File, String)]],
    nestedDirectory: SettingKey[String]): Setting[_] =
    siteMappings ++= {
      for ((f, d) <- mappings.value) yield (f, nestedDirectory.value + "/" + d)
    }

  def selectSubpaths(dir: File, filter: FileFilter): Seq[(File, String)] =
    Path.selectSubpaths(dir, filter).toSeq

  def copySite(dir: File, cacheDir: File, maps: Seq[(File, String)]): File = {
    val concrete = maps map { case (file, dest) => (file, dir / dest) }
    Sync.sync(CacheStore(cacheDir / "make-site"))(concrete)
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
    Compat.watchSettings(config)

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

  /**
    * Get short X.Y version
    */
  def shortVersion(full: String): String = full match {
    case VersionNumber(Seq(x, y, _*), _, _) => s"$x.$y"
    case _ => full
  }
}
