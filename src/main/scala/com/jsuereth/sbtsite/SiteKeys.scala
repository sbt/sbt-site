package com.jsuereth.sbtsite

import sbt._
import Keys._


object SiteKeys {
  val makeSite = TaskKey[File]("make-site", "Generates a static website for a project.")
  // Helper to point at mappings for the site.
  val siteMappings = mappings in makeSite
  val siteDirectory = target in makeSite
  val siteSources = sources in makeSite
  val siteSourceDirectory = sourceDirectory in makeSite
}