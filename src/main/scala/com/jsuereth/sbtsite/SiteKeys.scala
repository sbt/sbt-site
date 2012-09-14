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
    
  val previewSite = TaskKey[Unit]("preview-site", "Launches a jetty server that serves your generated site from the target directory")
  val previewFixedPort = SettingKey[Option[Int]]("previewFixedPort") in previewSite
  val previewLaunchBrowser = SettingKey[Boolean]("previewLaunchBrowser") in previewSite
}
