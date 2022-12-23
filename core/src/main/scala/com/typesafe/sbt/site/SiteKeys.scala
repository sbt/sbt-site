package com.typesafe.sbt.site

import sbt.Keys._
import sbt._

/**
 * Top-level keys.
 * @since 10/29/15
 */
trait SiteKeys {
  val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
  val packageSite = TaskKey[File]("package-site", "Create a zip file of the website.")
  val siteSubdirName = SettingKey[String]("siteSubdirName",
      "Name of subdirectory in site target directory to put generator plugin content. Defaults to empty string.")
  val siteMappings = makeSite / mappings
  val siteDirectory = makeSite / target
  val siteSources = makeSite / sources
  val siteSourceDirectory = makeSite / sourceDirectory
}
