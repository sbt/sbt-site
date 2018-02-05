package com.typesafe.sbt.site.laika

import java.io.File

import sbt._
import Keys._
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin.autoImport.makeSite
import laika.sbt.LaikaPlugin.autoImport.{Laika, laikaHTML, laikaSite }
import laika.sbt.LaikaPlugin
import Path.relativeTo

object LaikaSitePlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger

  object autoImport {
    val LaikaSite = config("laikaSite")
  }
  import autoImport._

  override def projectSettings = laikaSettings(LaikaSite)

  /** Creates settings necessary for running Laika in the given configuration. */
  def laikaSettings(config: Configuration): Seq[Setting[_]] =
      inConfig(config)(
        Def.settings(
          LaikaPlugin.projectSettings,
          target in laikaSite := target.value,
          includeFilter := AllPassFilter,
          mappings := {
            val output = laikaSite.value
            output ** includeFilter.value --- output pair Path.relativeTo(output)
          },
          siteSubdirName := "",
          sourceDirectories in Laika := Seq(sourceDirectory.value)
        )
      ) ++
        SiteHelpers.directorySettings(config) ++
        SiteHelpers.watchSettings(config) ++
        SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)

  private def generate(target: File, inc: FileFilter, exc: FileFilter): Seq[(File, String)] = {
    // Figure out what was generated.
    val files = (target ** inc) --- (target ** exc) --- target
    files pair relativeTo(target)
  }
}
