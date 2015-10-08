package com.typesafe.sbt.site

import SitePlugin._
import SitePlugin.autoImport.siteSubdirName
import sbt.Keys._
import sbt._

import scala.util.matching.Regex
/** Provides ability to map values to `@` delimited variables. */
object PreprocessPlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  object autoImport {
    val Preprocess = config("preprocess")

    // Setting and task keys that can be used to set up preprocessing
    val preprocessExts = SettingKey[Set[String]](
      "preprocess-exts", "Extensions of files to preprocess (not including dot).")
    val preprocessVars = SettingKey[Map[String, String]](
      "preprocess-vars", "Replacements for preprocessing.")
    val preprocess = TaskKey[File]("preprocess", "Preprocess a directory of files.")
  }
  import autoImport._
  // Default variable replacement regex
  private[sbt] val Variable = """@(\w+)@""".r
  // Allows @@ -> @ replacement
  private[sbt] val defaultReplacements = Map("" -> "@")

  override def projectSettings: Seq[Setting[_]] = Seq(
    siteSourceDirectory in Preprocess := siteSourceDirectory.value,
    siteDirectory in Preprocess := siteDirectory.value,
    preprocessExts := Set("txt", "html", "md"),
    preprocessVars := Map("VERSION" -> version.value),
    includeFilter in Preprocess := AllPassFilter
  ) ++
    inConfig(Preprocess)(
      Seq(
        sourceDirectory := sourceDirectory.value / "site-preprocess",
        target := target.value / Preprocess.name,
        preprocess := simplePreprocess(
          sourceDirectory.value, target.value, streams.value.cacheDirectory, preprocessExts.value,
          preprocessVars.value, streams.value.log),
        mappings := gatherMappings(preprocess.value, includeFilter.value),
        siteSubdirName := ""
      )
    ) ++
    SiteHelpers.watchSettings(Preprocess) ++
    SiteHelpers.addMappingsToSiteDir(mappings in Preprocess, siteSubdirName in Preprocess)

  /**
   * Simple preprocessing of all files in a directory using `@variable@` replacements.
   */
  private[sbt] def simplePreprocess(
    sourceDir: File,
    targetDir: File,
    cacheFile: File,
    fileExts: Set[String],
    replacements: Map[String, String],
    log: Logger): File = {
    transformDirectory(
      sourceDir, targetDir, SiteHelpers.hasExtension(fileExts),
      SiteHelpers.transformFile(replaceVariable(Variable, replacements ++ defaultReplacements)),
      cacheFile, log)
  }

  /** Find out what was generated. */
  private[sbt] def gatherMappings(output: File, includeFilter: FileFilter) = {
    output ** includeFilter --- output pair relativeTo(output)
  }

  /**
   * Create a transformed version of all files in a directory, given a predicate and a transform function for each file.
   */
  private[sbt] def transformDirectory(
    sourceDir: File,
    targetDir: File,
    transformable: File => Boolean,
    transform: (File, File) => Unit,
    cache: File,
    log: Logger): File = {
    val runTransform = FileFunction.cached(cache)(FilesInfo.hash, FilesInfo.exists) { (in, out) =>
      val map = Path.rebase(sourceDir, targetDir)
      if (in.removed.nonEmpty || in.modified.nonEmpty) {
        log.info("Preprocessing directory %s..." format sourceDir)
        for (source <- in.removed; target <- map(source)) {
          IO delete target
        }
        val updated = for (source <- in.modified; target <- map(source)) yield {
          if (source.isFile) {
            if (transformable(source)) {
              transform(source, target)
            }
            else {
              IO.copyFile(source, target)
            }
          }
          target
        }
        log.info("Directory preprocessed: " + targetDir)
        updated
      }
      else {
        Set.empty
      }
    }
    val sources = (sourceDir ** AllPassFilter).get.toSet
    runTransform(sources)
    targetDir
  }
  /**
   * Simple variable replacement in a string.
   */
  private[sbt] def replaceVariable(regex: Regex, replacements: Map[String, String])
    (input: String): String = {
    def replacement(key: String): String = replacements.getOrElse(
      key, sys.error("No replacement value defined for: " + key))
    regex.replaceAllIn(input, m => replacement(m.group(1)))
  }
}
