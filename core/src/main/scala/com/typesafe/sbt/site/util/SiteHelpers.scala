package com.typesafe.sbt.site.util

import java.io.PrintWriter

import com.typesafe.sbt.site.{Compat, SitePlugin}
import com.typesafe.sbt.site.Compat._
import sbt.Keys._
import sbt._
import sbtcompat.PluginCompat
import sbtcompat.PluginCompat.{ *, given }
/**
 * Utility/support functions.
 */
object SiteHelpers {
  import SitePlugin.autoImport.siteMappings
  /** Convenience functions to add a task of mappings to a site under a nested directory. */
  def addMappingsToSiteDir(
    mappings: Def.Initialize[Task[Seq[(PluginCompat.FileRef, String)]]],
    nestedDirectory: SettingKey[String]): Setting[?] =
    siteMappings ++= Def.uncached {
      for ((f, d) <- mappings.value) yield (f, nestedDirectory.value + "/" + d)
    }

  def selectSubpaths(dir: File, filter: FileFilter): Seq[(File, String)] =
    Path.selectSubpaths(dir, filter).toSeq

  def copySite(dir: File, cacheDir: File, maps: Seq[(PluginCompat.FileRef, String)])(implicit conv: xsbti.FileConverter): File = {
    val concrete = maps map { case (ref, dest) => (PluginCompat.toFile(ref), dir / dest) }
    Sync.sync(CacheStore(cacheDir / "make-site"))(concrete)
    dir
  }

  def siteArtifact(name: String) = Artifact(name, Artifact.DocType, "zip", "site")

  def createSiteZip(siteDir: File, zipPath: PluginCompat.ArtifactPath, s: TaskStreams)(implicit conv: xsbti.FileConverter): PluginCompat.FileRef = {
    val zipFile = PluginCompat.artifactPathToFile(zipPath)
    val midnight = (System.currentTimeMillis() / 86400000) * 86400000
    IO.zip(Path.allSubpaths(siteDir), zipFile, Some(midnight))
    s.log.info("Site packaged: " + zipFile)
    PluginCompat.toFileRef(zipFile)
  }

  def directorySettings(config: Configuration): Seq[Setting[?]] =
    inConfig(config)(
      Seq(
        sourceDirectory := sourceDirectory.value / config.name,
        target := target.value / config.name
      ))

  def watchSettings(config: Configuration): Seq[Setting[?]] =
    Compat.watchSettings(ThisScope.copy(config = Select(config)))

  def watchSettings(scope: Scope): Seq[Setting[?]] =
    Compat.watchSettings(scope)

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
