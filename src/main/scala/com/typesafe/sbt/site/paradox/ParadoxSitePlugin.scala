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
  override def requires = SitePlugin && SbtWeb && ParadoxPlugin
  override def trigger = noTrigger
  object autoImport {
    val Paradox = config("paradox")
  }

  import autoImport._
  import ParadoxPlugin.autoImport._
  override def projectSettings = paradoxSettings(Compile)
  def paradoxSettings(config: Configuration): Seq[Setting[_]] = {
    val siteNameConfig = if (config == Compile) Paradox else config
    List(
      siteSubdirName in siteNameConfig := ""
    ) ++
    SiteHelpers.watchSettings(ThisScope.in(config, paradox.key)) ++
    SiteHelpers.addMappingsToSiteDir((target in (config, paradox)).map(SiteHelpers.selectSubpaths(_, AllPassFilter)), siteSubdirName in siteNameConfig)
  }
}
