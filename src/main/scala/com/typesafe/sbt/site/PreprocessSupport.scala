package com.typesafe.sbt.site

import java.io.PrintWriter

import com.typesafe.sbt.SbtSite.SiteKeys._
import sbt.Keys._
import sbt._

import scala.util.matching.Regex

object PreprocessSupport {
  val Preprocess = config("preprocess")
  // Default variable replacement regex
  private[sbt] val Variable = """@(\w+)@""".r
  // Allows @@ -> @ replacement
  private[sbt] val defaultReplacements = Map("" -> "@")

  // Setting and task keys that can be used to set up preprocessing
  val preprocessExts = SettingKey[Set[String]]("preprocess-exts", "Extensions of files to preprocess (not including dot).")
  val preprocessVars = SettingKey[Map[String, String]]("preprocess-vars", "Replacements for preprocessing.")
  val preprocess = TaskKey[File]("preprocess", "Preprocess a directory of files.")

  def settings(config: Configuration = Preprocess): Seq[Setting[_]] =
    Seq(
      siteSourceDirectory in Preprocess := siteSourceDirectory.value,
      siteDirectory in Preprocess := siteDirectory.value,
      preprocessExts := Set("txt", "html", "md"),
      preprocessVars := Map("VERSION" -> version.value),
      includeFilter in config := AllPassFilter
    ) ++ inConfig(config)(Seq(
      sourceDirectory := sourceDirectory.value / "site-preprocess",
      target := target.value / config.name,
      preprocess := simplePreprocess(sourceDirectory.value, target.value, streams.value.cacheDirectory, preprocessExts.value, preprocessVars.value, streams.value.log),
      mappings := gatherMappings(preprocess.value, includeFilter.value)
    )) ++ Generator.watchSettings(config)

  /**
   * Simple preprocessing of all files in a directory using `@variable@` replacements.
   */
  def simplePreprocess(sourceDir: File, targetDir: File, cacheFile: File, fileExts: Set[String], replacements: Map[String, String], log: Logger): File = {
    transformDirectory(sourceDir, targetDir, hasExtension(fileExts), transformFile(replaceVariable(Variable, replacements ++ defaultReplacements)), cacheFile, log)
  }

  /** Find out what was generated. */
  private[sbt] def gatherMappings(output: File, includeFilter: FileFilter) = {
    output ** includeFilter --- output pair relativeTo(output)
  }

  /**
   * Create a transformed version of all files in a directory, given a predicate and a transform function for each file.
   */
  def transformDirectory(sourceDir: File, targetDir: File, transformable: File => Boolean, transform: (File, File) => Unit, cache: File, log: Logger): File = {
    val runTransform = FileFunction.cached(cache)(FilesInfo.hash, FilesInfo.exists) { (in, out) =>
      val map = Path.rebase(sourceDir, targetDir)
      if (in.removed.nonEmpty || in.modified.nonEmpty) {
        log.info("Preprocessing directory %s..." format sourceDir)
        for (source <- in.removed; target <- map(source)) {
          IO delete target
        }
        val updated = for (source <- in.modified; target <- map(source)) yield {
          if (source.isFile) {
            if (transformable(source)) transform(source, target)
            else IO.copyFile(source, target)
          }
          target
        }
        log.info("Directory preprocessed: " + targetDir)
        updated
      } else Set.empty
    }
    val sources = (sourceDir ***).get.toSet
    runTransform(sources)
    targetDir
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

  /**
   * Simple variable replacement in a string.
   */
  def replaceVariable(regex: Regex, replacements: Map[String, String])(input: String): String = {
    def replacement(key: String): String = replacements.getOrElse(key, sys.error("No replacement value defined for: " + key))
    regex.replaceAllIn(input, m => replacement(m.group(1)))
  }
}
