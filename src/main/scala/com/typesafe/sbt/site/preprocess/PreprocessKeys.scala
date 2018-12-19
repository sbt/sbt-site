package com.typesafe.sbt.site.preprocess

import sbt._

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/**
 * Keys for the preprocess sub-plugin.
 * @since 10/29/15
 */
trait PreprocessKeys {
  val preprocessIncludeFilter = SettingKey[FileFilter](
      "preprocessIncludeFilter", "Filter defining set of files to preprocess")
  val preprocessVars = SettingKey[Map[String, String]](
      "preprocessVars", "Replacements for preprocessing.")
   val preprocessRules = SettingKey[Seq[(Regex, Match => String)]]("preprocessRules", "Rules for preprocessing supporting Regular Expressions")
  val preprocess = TaskKey[File]("preprocess", "Preprocess a directory of files.")
}
