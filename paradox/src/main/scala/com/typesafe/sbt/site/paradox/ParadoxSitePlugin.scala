package com.typesafe.sbt.site.paradox

import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import sbt.Keys._
import sbt._
import com.lightbend.paradox.sbt.ParadoxPlugin
import com.typesafe.sbt.web.SbtWeb
import sbtcompat.PluginCompat

/** Paradox generator. */
object ParadoxSitePlugin extends AutoPlugin {
  override def requires = SitePlugin && SbtWeb && ParadoxPlugin
  override def trigger = noTrigger
  object autoImport {
    val Paradox = config("paradox")
  }

  import autoImport._
  import ParadoxPlugin.autoImport._
  override def projectSettings = paradoxSettings(Compile)
  def paradoxSettings(config: Configuration): Seq[Setting[?]] = {
    val siteNameConfig = if (config == Compile) Paradox else config
    List(
      siteNameConfig / siteSubdirName := ""
    ) ++
    SiteHelpers.watchSettings(ThisScope.copy(config = Select(config), task = Select(paradox.key))) ++
    SiteHelpers.addMappingsToSiteDir(
      Def.task {
        implicit val conv: xsbti.FileConverter = fileConverter.value
        PluginCompat.toFileRefsMapping(SiteHelpers.selectSubpaths((config / paradox).value, AllPassFilter))
      },
      siteNameConfig / siteSubdirName
    )
  }
}
