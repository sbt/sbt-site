package com.typesafe.sbt.site

import sbt.Keys._
import sbt._

/**
 * Top-level keys.
 * @since 10/29/15
 */
trait SiteKeys {
  val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
  val packageSite = TaskKey[File]("packageSite", "Create a zip file of the website.")
  val siteSubdirName = SettingKey[String]("siteSubdirName",
      "Name of subdirectory in site target directory to put generator plugin content. Defaults to empty string.")
  val siteMappings = mappings in makeSite
  val siteDirectory = target in makeSite
  val siteSources = sources in makeSite
  val siteSourceDirectory = sourceDirectory in makeSite
}
