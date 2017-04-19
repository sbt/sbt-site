package com.typesafe.sbt.site.paradox

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._
import com.lightbend.paradox.sbt.ParadoxPlugin
import com.typesafe.sbt.web.SbtWeb

/** Paradox generator. */
object ParadoxSitePlugin extends AutoPlugin {
  override def requires = SitePlugin && SbtWeb
  override def trigger = noTrigger
  object autoImport {
    val Paradox = config("paradox")
  }

  import autoImport._
  import ParadoxPlugin.autoImport._
  override def projectSettings = paradoxSettings(Paradox)
  def paradoxSettings(config: Configuration): Seq[Setting[_]] =
    ParadoxPlugin.paradoxSettings(config) ++
    inConfig(config)(
      List(
        sourceDirectory in paradox := {
          val sd = sourceDirectory.value
          if (sd.toString endsWith "/paradox/paradox") new File(sd.toString dropRight "/paradox".length) else sd
        },
        includeFilter := AllPassFilter,
        mappings := {
          val _ = paradox.value
          val output = (target in paradox).value
          output ** includeFilter.value --- output pair relativeTo(output)
        },
        siteSubdirName := ""
      )
    ) ++
    SiteHelpers.directorySettings(config) ++
    SiteHelpers.watchSettings(config) ++
    SiteHelpers.addMappingsToSiteDir(mappings in config, siteSubdirName in config)
}
